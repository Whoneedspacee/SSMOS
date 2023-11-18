package ssm.attributes;

import ssm.events.SmashDamageEvent;
import ssm.managers.ownerevents.OwnerTakeSmashDamageEvent;
import ssm.utilities.Utils;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class DeadlyBones extends Attribute implements OwnerTakeSmashDamageEvent {

    private long last_spawn_time = 0;
    protected double damage = 4;
    protected double knockback_multiplier = 2.5;
    protected double hitbox_radius = 4;
    protected long ticks_to_explode = 50;
    protected long spawn_rate_ms = 400;

    public DeadlyBones() {
        super();
        this.name = "Deadly Bones";
        this.usage = AbilityUsage.PASSIVE;
        this.description = new String[] {
                ChatColor.RESET + "Whenever you take damage, you drop a bone",
                ChatColor.RESET + "which will explode after a few seconds",
                ChatColor.RESET + "dealing damage and knockback to enemies."
        };
    }

    public void activate() {
        BukkitRunnable runnable = new BukkitRunnable() {
            private Item bone = owner.getWorld().dropItemNaturally(owner.getLocation().add(0, 0.5, 0), new ItemStack(Material.BONE));
            private long drop_item = System.currentTimeMillis();

            @Override
            public void run() {
                if(owner == null) {
                    bone.remove();
                    cancel();
                    return;
                }
                if(bone.isValid() && bone.getTicksLived() < ticks_to_explode) {
                    return;
                }
                bone.setPickupDelay(1000000);
                Utils.playParticle(EnumParticle.EXPLOSION_LARGE, bone.getLocation(),
                        0, 0, 0, 0, 1, 96, bone.getWorld().getPlayers());
                bone.getWorld().playSound(bone.getLocation(), Sound.EXPLODE, 0.8f, 1.4f);
                HashMap<LivingEntity, Double> targets = Utils.getInRadius(bone.getLocation(), hitbox_radius);
                for(LivingEntity livingEntity : targets.keySet()) {
                    if(livingEntity.equals(owner)) {
                        continue;
                    }
                    double distance_damage = damage * targets.get(livingEntity) + 1;
                    SmashDamageEvent smashDamageEvent = new SmashDamageEvent(livingEntity, owner, distance_damage);
                    smashDamageEvent.multiplyKnockback(knockback_multiplier);
                    smashDamageEvent.setIgnoreDamageDelay(true);
                    smashDamageEvent.setReason(name);
                    smashDamageEvent.callEvent();
                }
                bone.remove();
                cancel();
            }
        };
        runnable.runTaskTimer(plugin, 0L, 0L);
    }

    @Override
    public void onOwnerTakeSmashDamageEvent(SmashDamageEvent e) {
        if(e.isCancelled() || e.getDamageCause() == DamageCause.STARVATION) {
            return;
        }
        if(e.getDamage() <= 0) {
            return;
        }
        if(System.currentTimeMillis() - last_spawn_time < spawn_rate_ms) {
            return;
        }
        last_spawn_time = System.currentTimeMillis();
        checkAndActivate();
    }
}
