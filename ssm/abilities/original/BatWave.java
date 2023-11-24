package ssm.abilities.original;

import ssm.abilities.Ability;
import ssm.events.SmashDamageEvent;
import ssm.managers.DisguiseManager;
import ssm.managers.disguises.Disguise;
import ssm.managers.ownerevents.OwnerDeathEvent;
import ssm.managers.ownerevents.OwnerRightClickEvent;
import ssm.utilities.DamageUtil;
import ssm.utilities.Utils;
import ssm.utilities.VelocityUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class BatWave extends Ability implements OwnerRightClickEvent, OwnerDeathEvent {

    private int bat_task = -1;
    private Location start_location = null;
    private long start_time = 0;
    private long last_leash_time = 0;
    private double damage_taken = 0;
    private long last_bat_hit_time = 0;
    protected List<Bat> bats = new ArrayList<Bat>();
    protected double damage = 3;
    protected double hitbox = 2;
    protected double disable_damage = 20;
    protected double knockback = 1.75;
    protected long bat_hit_cooldown_ms = 200;

    public BatWave() {
        super();
        this.name = "Bat Wave";
        this.cooldownTime = 8;
        this.description = new String[] {
                ChatColor.RESET + "Release a wave of bats which give",
                ChatColor.RESET + "damage and knockback to anything they",
                ChatColor.RESET + "collide with.",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        if (Bukkit.getScheduler().isQueued(bat_task) || Bukkit.getScheduler().isCurrentlyRunning(bat_task)) {
            if(System.currentTimeMillis() - last_leash_time < 500) {
                return;
            }
            last_leash_time = System.currentTimeMillis();
            if(bats.size() <= 0) {
                return;
            }
            boolean should_leash = !bats.get(0).isLeashed();
            if(should_leash) {
                leashBats();
                return;
            }
            unleashBats();
            return;
        }
        checkAndActivate();
    }

    public void activate() {
        clearBats();
        start_location = owner.getEyeLocation();
        start_time = System.currentTimeMillis();
        damage_taken = 0;
        for(int i = 0; i < 32; i++) {
            Bat bat = owner.getWorld().spawn(owner.getEyeLocation(), Bat.class);
            bats.add(bat);
        }
        bat_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(System.currentTimeMillis() - start_time >= 2500) {
                    clearBats();
                    return;
                }
                Vector bat_vector = new Vector(0, 0, 0);
                double bat_count = 0;
                for(Bat bat : bats) {
                    if(!bat.isValid()) {
                        continue;
                    }
                    bat_vector.add(bat.getLocation().toVector());
                    bat_count++;
                    Vector random = new Vector((Math.random() - 0.5) / 2, (Math.random() - 0.5) / 2, (Math.random() - 0.5) / 2);
                    bat.setVelocity(start_location.getDirection().clone().multiply(0.5).add(random));
                    for(Player other : bat.getWorld().getPlayers()) {
                        if(System.currentTimeMillis() - last_bat_hit_time < bat_hit_cooldown_ms) {
                            break;
                        }
                        if(other.equals(owner)) {
                            continue;
                        }
                        if(!DamageUtil.canDamage(other, owner)) {
                            continue;
                        }
                        if(!Utils.hitBox(bat.getLocation(), other, hitbox, null)) {
                            continue;
                        }
                        SmashDamageEvent smashDamageEvent = new SmashDamageEvent(other, owner, damage);
                        smashDamageEvent.multiplyKnockback(knockback);
                        smashDamageEvent.setIgnoreDamageDelay(true);
                        smashDamageEvent.setReason(name);
                        smashDamageEvent.callEvent();
                        bat.getWorld().playSound(bat.getLocation(), Sound.BAT_HURT, 1f, 1f);
                        Utils.playParticle(EnumParticle.SMOKE_LARGE, bat.getLocation(),
                                0, 0, 0, 0, 3, 96, bat.getWorld().getPlayers());
                        bat.remove();
                        last_bat_hit_time = System.currentTimeMillis();
                    }
                }
                if(bats.size() > 0 && bats.get(0).isLeashed()) {
                    bat_vector.multiply(1 / bat_count);
                    Location bat_location = bat_vector.toLocation(owner.getWorld());
                    Vector difference = bat_location.toVector().subtract(owner.getLocation().toVector()).normalize();
                    VelocityUtil.setVelocity(owner, difference, 0.35, false, 0, 0, 10, false);
                }
            }
        }, 0L, 0L);
    }

    public void leashBats() {
        Disguise disguise = DisguiseManager.disguises.get(owner);
        for(Bat bat : bats) {
            bat.setLeashHolder(owner);
            if(disguise != null && disguise.getLiving() != null) {
                disguise.setAsLeashHolder(bat);
            }
        }
    }

    public void unleashBats() {
        Disguise disguise = DisguiseManager.disguises.get(owner);
        for(Bat bat : bats) {
            bat.setLeashHolder(null);
            if(disguise != null && disguise.getLiving() != null) {
                disguise.removeLeashHolder(bat);
            }
        }
    }

    public void clearBats() {
        if (Bukkit.getScheduler().isQueued(bat_task) || Bukkit.getScheduler().isCurrentlyRunning(bat_task)) {
            Bukkit.getScheduler().cancelTask(bat_task);
        }
        unleashBats();
        for(Bat bat : bats) {
            if(bat.isValid()) {
                Utils.playParticle(EnumParticle.SMOKE_LARGE, bat.getLocation(),
                        0, 0, 0, 0, 3, 96, bat.getWorld().getPlayers());
            }
            bat.remove();
        }
        bats.clear();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onOwnerTakeSmashDamageEvent(SmashDamageEvent e) {
        if(e.isCancelled()) {
            return;
        }
        if(!(e.getDamagee() instanceof Player)) {
            return;
        }
        if(!e.getDamagee().equals(owner)) {
            return;
        }
        damage_taken += e.getDamage();
        if(e.getDamageCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }
        if(damage_taken < disable_damage) {
            return;
        }
        clearBats();
    }

    @Override
    public void onOwnerDeathEvent() {
        clearBats();
    }

}