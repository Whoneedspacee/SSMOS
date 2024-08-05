package xyz.whoneedspacee.ssmos.abilities.original;

import xyz.whoneedspacee.ssmos.managers.ownerevents.OwnerRightClickEvent;
import xyz.whoneedspacee.ssmos.abilities.Ability;
import xyz.whoneedspacee.ssmos.events.SmashDamageEvent;
import xyz.whoneedspacee.ssmos.managers.CooldownManager;
import xyz.whoneedspacee.ssmos.utilities.VelocityUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import xyz.whoneedspacee.ssmos.attributes.Attribute;

public class EggBlaster extends Ability implements OwnerRightClickEvent {

    private int egg_task = -1;
    protected long duration_ms = 750;
    protected double damage = 1;

    public EggBlaster() {
        super();
        this.name = "Egg Blaster";
        this.usage = Attribute.AbilityUsage.HOLD_BLOCKING;
        this.cooldownTime = 2.5;
        this.description = new String[] {
                ChatColor.RESET + "Unleash a barrage of your precious eggs.",
                ChatColor.RESET + "They won't deal any knockback, but they",
                ChatColor.RESET + "can deal some serious damage.",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        if(Bukkit.getScheduler().isQueued(egg_task) || Bukkit.getScheduler().isCurrentlyRunning(egg_task)) {
            return;
        }
        checkAndActivate();
    }

    public void activate() {
        egg_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(owner == null || !owner.isBlocking()) {
                    Bukkit.getScheduler().cancelTask(egg_task);
                    return;
                }
                if(CooldownManager.getInstance().getTimeElapsedFor(EggBlaster.this, owner) >= duration_ms) {
                    Bukkit.getScheduler().cancelTask(egg_task);
                    return;
                }
                Vector offset = owner.getLocation().getDirection();
                if(offset.getY() < 0) {
                    offset.setY(0);
                }
                Egg egg = owner.getWorld().spawn(owner.getLocation().add(0, 0.5, 0).add(offset), Egg.class);
                egg.setVelocity(owner.getLocation().getDirection().add(new Vector(0, 0.2, 0)));
                egg.setShooter(owner);
                owner.getWorld().playSound(owner.getLocation(), Sound.CHICKEN_EGG_POP, 0.5f, 1f);
            }
        }, 0L, 0L);
    }

    // Lowest event priority so egg doesn't put us on the damage rate delay
    @EventHandler(priority = EventPriority.LOWEST)
    public void eggHit(SmashDamageEvent e) {
        if(!(e.getProjectile() instanceof Egg)) {
            return;
        }
        if(!(e.getDamager() instanceof Player)) {
            return;
        }
        if(!e.getDamager().equals(owner)) {
            return;
        }
        e.setDamage(damage);
        e.setIgnoreDamageDelay(true);
        e.multiplyKnockback(0);
        e.setReason(name);
        VelocityUtil.setVelocity(e.getDamagee(), new Vector(0, 0, 0));
    }

}