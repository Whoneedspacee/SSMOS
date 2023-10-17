package SSM.Attributes;

import SSM.GameManagers.OwnerEvents.EntityDamageOwnerEvent;
import SSM.GameManagers.OwnerEvents.OwnerTakeDamageEvent;
import SSM.Utilities.DamageUtil;
import SSM.Utilities.ServerMessageType;
import SSM.Utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class LightningShield extends Attribute implements OwnerTakeDamageEvent, EntityDamageOwnerEvent {

    private boolean active = false;
    private long ticks_remaining = 0;

    public LightningShield() {
        super();
        this.name = "Lightning Shield";
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                ticks_remaining--;
                if (ticks_remaining <= 0 && active) {
                    deactivate();
                }
            }
        }, 0L, 0L);
    }

    @Override
    public void activate() {
        active = true;
        ticks_remaining = 40;
        owner.getWorld().playSound(owner.getLocation(), Sound.CREEPER_HISS, 3f, 1.25f);
        Utils.sendAttributeMessage("You gained", name, owner, ServerMessageType.SKILL);
        // Disguise Stuff
    }

    public void deactivate() {
        active = false;
        owner.getWorld().playSound(owner.getLocation(), Sound.CREEPER_HISS, 3f, 0.75f);
        // Disguise Stuff
    }

    @Override
    public void onOwnerTakeDamage(EntityDamageEvent e) {
        if (e.isCancelled()) {
            return;
        }
        // This happens after EntityDamageByEntityEvent so this is fine
        if (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK &&
                e.getCause() != EntityDamageEvent.DamageCause.FIRE_TICK) {
            activate();
        }
    }

    @Override
    public void onEntityDamageOwner(EntityDamageByEntityEvent e) {
        if (active && e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            if (e.getDamager() instanceof LivingEntity) {
                LivingEntity target = (LivingEntity) e.getDamager();
                doLightning(target);
                e.setCancelled(true);
                return;
            }
        }
    }

    public void doLightning(LivingEntity target) {
        owner.getWorld().strikeLightningEffect(owner.getLocation());
        DamageUtil.damage(target, owner, 4, 2.5, false, EntityDamageEvent.DamageCause.CUSTOM, null, true);
        deactivate();
        Utils.sendAttributeMessage("You hit §e" + target.getName() + " §7with",
                name, owner, ServerMessageType.SKILL);
    }

}
