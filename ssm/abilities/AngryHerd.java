package ssm.abilities;

import ssm.events.SmashDamageEvent;
import ssm.managers.CooldownManager;
import ssm.managers.ownerevents.OwnerRightClickEvent;
import ssm.utilities.DamageUtil;
import ssm.utilities.Utils;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AngryHerd extends Ability implements OwnerRightClickEvent {

    private int herd_task = -1;
    private HashMap<Entity, Vector> cow_directions = new HashMap<Entity, Vector>();
    private HashMap<Entity, Location> last_cow_location = new HashMap<Entity, Location>();
    private HashMap<Entity, Long> last_move_time = new HashMap<Entity, Long>();
    protected HashMap<Player, Long> last_damage_time = new HashMap<Player, Long>();
    protected long stuck_time_ms = 300;
    protected long force_move_time_ms = 350;
    protected long duration_ms = 2500;
    protected double hitbox_radius = 2.2;
    protected long damage_cooldown_ms = 600;
    protected double damage = 5;
    protected int cow_amount_radius = 3;
    protected int cow_amount_height = 1;
    protected double knockback = 1.25;

    public AngryHerd() {
        super();
        this.name = "Angry Herd";
        this.cooldownTime = 13;
        this.description = new String[] {
                ChatColor.RESET + "Send forth an angry herd of Cows",
                ChatColor.RESET + "which deal damage and knockback",
                ChatColor.RESET + "to opponents. Can hit multiple times."
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        cow_directions.clear();
        last_cow_location.clear();
        last_move_time.clear();
        last_damage_time.clear();
        List<Entity> cows = new ArrayList<Entity>();
        for(int j = 0; j < cow_amount_height; j++) {
            for (int i = 1 - cow_amount_radius; i < cow_amount_radius; i++) {
                Vector direction = owner.getLocation().getDirection();
                direction.setY(0);
                direction.normalize();
                Location cow_location = owner.getLocation();
                cow_location.add(direction);
                cow_location.add(new Vector(-direction.getZ(), 0, direction.getX()).multiply(i * 1.5));
                cow_location.add(new Vector(0, j, 0));
                Cow cow = owner.getWorld().spawn(cow_location, Cow.class);
                cows.add(cow);
                Vector cow_direction = owner.getLocation().getDirection();
                cow_direction.setY(0);
                cow_direction.normalize();
                cow_direction.multiply(0.75);
                cow_direction.setY(-0.2);
                cow_directions.put(cow, cow_direction);
                last_cow_location.put(cow, cow_location);
                last_move_time.put(cow, System.currentTimeMillis());
            }
        }
        owner.getWorld().playSound(owner.getLocation(), Sound.COW_IDLE, 2f, 0.6f);
        if (Bukkit.getScheduler().isQueued(herd_task) || Bukkit.getScheduler().isCurrentlyRunning(herd_task)) {
            Bukkit.getScheduler().cancelTask(herd_task);
        }
        herd_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(owner == null || CooldownManager.getInstance().getTimeElapsedFor(AngryHerd.this, owner) >= duration_ms) {
                    Bukkit.getScheduler().cancelTask(herd_task);
                    for(Entity cow : cows) {
                        if(cow.isValid()) {
                            Utils.playParticle(EnumParticle.EXPLOSION_NORMAL, cow.getLocation().add(0, 1, 0),
                                    0, 0, 0, 0, 1, 96, cow.getWorld().getPlayers());
                            cow.remove();
                        }
                    }
                    return;
                }
                for(Entity cow : cows) {
                    if(cow.getLocation().distance(last_cow_location.get(cow)) > 1) {
                        last_cow_location.put(cow, cow.getLocation());
                        last_move_time.put(cow, System.currentTimeMillis());
                    }
                    if(System.currentTimeMillis() - last_move_time.get(cow) >= stuck_time_ms) {
                        if(cow.isValid()) {
                            Utils.playParticle(EnumParticle.EXPLOSION_NORMAL, cow.getLocation().add(0, 1, 0),
                                    0, 0, 0, 0, 1, 96, cow.getWorld().getPlayers());
                            cow.remove();
                        }
                        continue;
                    }
                    if(Utils.entityIsOnGround(cow)) {
                        cow_directions.put(cow, cow_directions.get(cow).setY(-0.1));
                    }
                    else {
                        cow_directions.put(cow, cow_directions.get(cow).setY(Math.max(-1, cow_directions.get(cow).getY() - 0.03)));
                    }
                    if(Utils.entityIsOnGround(cow) && System.currentTimeMillis() - last_move_time.get(cow) >= force_move_time_ms) {
                        cow.setVelocity(cow_directions.get(cow).clone().add(new Vector(0, 0.75, 0)));
                    }
                    else {
                        cow.setVelocity(cow_directions.get(cow));
                    }
                    if(Math.random() > 0.99) {
                        cow.getWorld().playSound(cow.getLocation(), Sound.COW_IDLE, 1f, 1f);
                    }
                    if(Math.random() > 0.97) {
                        cow.getWorld().playSound(cow.getLocation(), Sound.COW_WALK, 1f, 1.2f);
                    }
                    for(Player player : owner.getWorld().getPlayers()) {
                        if(player.equals(owner)) {
                            continue;
                        }
                        if(!DamageUtil.canDamage(player, owner)) {
                            continue;
                        }
                        if(cow.getLocation().distance(player.getLocation()) >= hitbox_radius) {
                            continue;
                        }
                        last_damage_time.putIfAbsent(player, 0L);
                        if(System.currentTimeMillis() - last_damage_time.get(player) < damage_cooldown_ms) {
                            continue;
                        }
                        last_damage_time.put(player, System.currentTimeMillis());
                        SmashDamageEvent smashDamageEvent = new SmashDamageEvent(player, owner, damage);
                        smashDamageEvent.multiplyKnockback(knockback);
                        smashDamageEvent.setIgnoreDamageDelay(true);
                        smashDamageEvent.setReason(name);
                        smashDamageEvent.callEvent();
                        Utils.playParticle(EnumParticle.EXPLOSION_LARGE, cow.getLocation().add(0, 1, 0),
                                1f, 1f, 1f, 0, 12, 96, cow.getWorld().getPlayers());
                        cow.getWorld().playSound(cow.getLocation(), Sound.ZOMBIE_WOOD, 0.75f, 0.8f);
                        cow.getWorld().playSound(cow.getLocation(), Sound.COW_HURT, 1.5f, 0.75f);
                    }
                }
            }
        }, 0L, 0L);
    }

}