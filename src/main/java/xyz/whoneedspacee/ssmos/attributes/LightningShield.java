package xyz.whoneedspacee.ssmos.attributes;

import org.bukkit.entity.LivingEntity;
import xyz.whoneedspacee.ssmos.managers.disguises.CreeperDisguise;
import xyz.whoneedspacee.ssmos.managers.disguises.Disguise;
import xyz.whoneedspacee.ssmos.events.SmashDamageEvent;
import xyz.whoneedspacee.ssmos.managers.DisguiseManager;
import xyz.whoneedspacee.ssmos.managers.ownerevents.OwnerTakeSmashDamageEvent;
import xyz.whoneedspacee.ssmos.utilities.ServerMessageType;
import xyz.whoneedspacee.ssmos.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Sound;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class LightningShield extends Attribute implements OwnerTakeSmashDamageEvent {

    private boolean active = false;
    private int unpower_task = -1;

    public LightningShield() {
        super();
        this.name = "Lightning Shield";
        this.usage = AbilityUsage.PASSIVE;
        this.useMessage = "You gained";
        this.description = new String[] {
                ChatColor.RESET + "When attacked by a non-melee attack,",
                ChatColor.RESET + "you gain Lightning Shield for 2 seconds.",
                ChatColor.RESET + "",
                ChatColor.RESET + "Lightning Shield blocks 1 melee attack,",
                ChatColor.RESET + "striking lightning on the attacker.",
        };
    }

    public void activate() {
        owner.removePotionEffect(PotionEffectType.SPEED);
        owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 80, 1, false, false));
        owner.getWorld().playSound(owner.getLocation(), Sound.CREEPER_HISS, 3f, 1.25f);
        Utils.sendAttributeMessage(this, owner, ServerMessageType.SKILL);
        if(Bukkit.getScheduler().isQueued(unpower_task) || Bukkit.getScheduler().isCurrentlyRunning(unpower_task)) {
            Bukkit.getScheduler().cancelTask(unpower_task);
        }
        setPowered();
        unpower_task = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(active && owner != null) {
                    setUnpowered();
                    owner.getWorld().playSound(owner.getLocation(), Sound.CREEPER_HISS, 3f, 0.75f);
                }
            }
        }, 40L);
    }

    @Override
    public void onOwnerTakeSmashDamageEvent(SmashDamageEvent e) {
        if(e.isCancelled()) {
            return;
        }
        if(e.getDamageCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            if(active) {
                e.setCancelled(true);
                Utils.sendAttributeMessage("You hit " + ChatColor.YELLOW + e.getDamager().getName()
                        + ChatColor.GRAY + " with", name, owner, ServerMessageType.SKILL);
                owner.getWorld().strikeLightningEffect(owner.getLocation());
                setUnpowered();
                // 1 Second Shock Duration
                BukkitRunnable shock_runnable = new BukkitRunnable() {
                    int ticks = 0;
                    LivingEntity damager = e.getDamager();
                    @Override
                    public void run() {
                        if(ticks >= 20 || damager == null) {
                            cancel();
                            return;
                        }
                        damager.playEffect(EntityEffect.HURT);
                        ticks++;
                    }
                };
                shock_runnable.runTaskTimer(plugin, 0L, 0L);
                SmashDamageEvent smashDamageEvent = new SmashDamageEvent(e.getDamager(), owner, 4);
                smashDamageEvent.setDamage(4);
                smashDamageEvent.multiplyKnockback(2.5);
                smashDamageEvent.setIgnoreDamageDelay(true);
                smashDamageEvent.setReason(name);
                smashDamageEvent.callEvent();
            }
            return;
        }
        if(e.getDamageCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
            return;
        }
        if(e.getDamageCause() == EntityDamageEvent.DamageCause.STARVATION) {
            return;
        }
        if(e.getDamageCause() == EntityDamageEvent.DamageCause.POISON) {
            return;
        }
        checkAndActivate();
    }

    public void setUnpowered() {
        active = false;
        Disguise disguise = DisguiseManager.disguises.get(owner);
        if(!(disguise instanceof CreeperDisguise)) {
            return;
        }
        CreeperDisguise creeperDisguise = (CreeperDisguise) disguise;
        creeperDisguise.setPoweredState((byte) 0);
    }

    public void setPowered() {
        active = true;
        Disguise disguise = DisguiseManager.disguises.get(owner);
        if(!(disguise instanceof CreeperDisguise)) {
            return;
        }
        CreeperDisguise creeperDisguise = (CreeperDisguise) disguise;
        creeperDisguise.setPoweredState((byte) 1);
    }

}
