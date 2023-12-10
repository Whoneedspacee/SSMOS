package ssm.abilities.original;

import ssm.abilities.Ability;
import ssm.events.SmashDamageEvent;
import ssm.managers.CooldownManager;
import ssm.managers.ownerevents.OwnerRightClickEvent;
import ssm.managers.ownerevents.OwnerTakeSmashDamageEvent;
import ssm.utilities.DamageUtil;
import ssm.utilities.ServerMessageType;
import ssm.utilities.Utils;
import ssm.utilities.VelocityUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class Firefly extends Ability implements OwnerRightClickEvent, OwnerTakeSmashDamageEvent {

    private int firefly_task = -1;
    private HashMap<Player, Long> last_hit_time = new HashMap<Player, Long>();
    protected double damage = 7;
    protected double knockback_multiplier = 2;
    protected double hitbox_radius = 4;
    protected double velocity = 0.7;
    protected long damage_delay_ms = 2000;
    protected double minimum_cancel_damage = 4;
    public long warmup_time_ms = 800;
    public long end_time_ms = 2050;

    public Firefly() {
        super();
        this.name = "Firefly";
        this.cooldownTime = 8;
        this.description = new String[] {
                ChatColor.RESET + "After a short startup time, you fly",
                ChatColor.RESET + "forward with great power, destroying",
                ChatColor.RESET + "anyone you touch.",
                ChatColor.RESET + "",
                ChatColor.RESET + "If you are hit by a projectile during",
                ChatColor.RESET + "startup time, the skill is cancelled.",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        if(Bukkit.getScheduler().isQueued(firefly_task) || Bukkit.getScheduler().isCurrentlyRunning(firefly_task)) {
            Bukkit.getScheduler().cancelTask(firefly_task);
        }
        firefly_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            private int ticks = 0;

            @Override
            public void run() {
                ticks++;
                if(owner == null) {
                    Bukkit.getScheduler().cancelTask(firefly_task);
                    return;
                }
                if(CooldownManager.getInstance().getTimeElapsedFor(Firefly.this, owner) > end_time_ms) {
                    Bukkit.getScheduler().cancelTask(firefly_task);
                    return;
                }
                if(CooldownManager.getInstance().getTimeElapsedFor(Firefly.this, owner) < warmup_time_ms) {
                    VelocityUtil.setVelocity(owner, new Vector(0, 0, 0));
                    owner.getWorld().playSound(owner.getLocation(), Sound.EXPLODE, 0.2f, 0.6f);
                    Utils.playParticle(EnumParticle.FIREWORKS_SPARK, owner.getLocation().add(0, 1, 0),
                            0.6f, 0.6f, 0.6f, 0, 10, 96, owner.getWorld().getPlayers());
                    float progress = (float) CooldownManager.getInstance().getTimeElapsedFor(Firefly.this, owner) / warmup_time_ms;
                    owner.getWorld().playSound(owner.getLocation(), Sound.BLAZE_BREATH, 0.5f, 1f + progress);
                    return;
                }
                VelocityUtil.setVelocity(owner, owner.getLocation().getDirection().multiply(velocity).add(new Vector(0, 0.15, 0)));
                owner.getWorld().playSound(owner.getLocation(), Sound.EXPLODE, 0.6f, 1.2f);
                Utils.playParticle(EnumParticle.FLAME, owner.getLocation().add(0, 1, 0),
                        1f, 1f, 1f, 0, 15, 96, owner.getWorld().getPlayers());
                Utils.playParticle(EnumParticle.LAVA, owner.getLocation().add(0, 1, 0),
                        1f, 1f, 1f, 0, 10, 96, owner.getWorld().getPlayers());
                owner.getWorld().playSound(owner.getLocation(), Sound.EXPLODE, 0.75f, 0.75f);
                for(Player player : Utils.getNearby(owner.getLocation(), hitbox_radius)) {
                    if(player.equals(owner)) {
                        continue;
                    }
                    if(!DamageUtil.canDamage(player, owner)) {
                        continue;
                    }
                    player.playEffect(EntityEffect.HURT);
                    if(ticks % 12 == 0) {
                        last_hit_time.putIfAbsent(player, 0L);
                        if(System.currentTimeMillis() - last_hit_time.get(player) < damage_delay_ms) {
                            continue;
                        }
                        last_hit_time.put(player, System.currentTimeMillis());
                        Utils.sendAttributeMessage(ChatColor.YELLOW + owner.getName() +
                                ChatColor.GRAY + " hit you with", name, player, ServerMessageType.GAME);
                        SmashDamageEvent smashDamageEvent = new SmashDamageEvent(player, owner, damage);
                        smashDamageEvent.multiplyKnockback(knockback_multiplier);
                        smashDamageEvent.setIgnoreDamageDelay(true);
                        smashDamageEvent.setReason(name);
                        smashDamageEvent.callEvent();
                    }
                }
            }
        }, 0L, 0L);
    }

    @Override
    public void onOwnerTakeSmashDamageEvent(SmashDamageEvent e) {
        if(e.isCancelled()) {
            return;
        }
        if(!Bukkit.getScheduler().isQueued(firefly_task) && !Bukkit.getScheduler().isCurrentlyRunning(firefly_task)) {
            return;
        }
        if(CooldownManager.getInstance().getTimeElapsedFor(this, owner) < warmup_time_ms) {
            if(e.getDamage() >= minimum_cancel_damage) {
                Bukkit.getScheduler().cancelTask(firefly_task);
            }
            return;
        }
        e.setCancelled(true);
    }
}