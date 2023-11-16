package ssm.abilities;

import ssm.events.SmashDamageEvent;
import ssm.gamemanagers.ownerevents.OwnerRightClickEvent;
import ssm.utilities.Utils;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;

public class GuidedWitherSkull extends Ability implements OwnerRightClickEvent {

    protected double damage = 10;
    protected double velocity = 0.6;
    protected double knockback = 1.5;
    protected double direct_radius = 1;
    protected double splash_radius = 6;

    public GuidedWitherSkull() {
        super();
        this.name = "Wither Skull";
        this.item_name = "Guided Wither Skull";
        this.usage = AbilityUsage.HOLD_BLOCKING;
        this.cooldownTime = 6;
        this.useMessage = "You launched";
        this.description = new String[]{
                ChatColor.RESET + "Launch a Wither Skull forwards, hold",
                ChatColor.RESET + "block to guide the missile!"
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        ArmorStand skull = owner.getWorld().spawn(owner.getLocation(), ArmorStand.class);
        ItemStack helmet = new ItemStack(Material.SKULL_ITEM);
        helmet.setDurability((short) 1);
        skull.setHelmet(helmet);
        skull.setGravity(false);
        skull.setVisible(false);
        skull.setMetadata("Wither Skull", new FixedMetadataValue(plugin, 1));
        owner.getWorld().playSound(owner.getLocation(), Sound.WITHER_SHOOT, 1f, 1f);
        // New runnable for each wither skull since they can stack up
        BukkitRunnable runnable = new BukkitRunnable() {
            private Vector skull_direction = owner.getLocation().getDirection().multiply(velocity);

            @Override
            public void run() {
                if (owner == null || !owner.isValid() || !skull.isValid()) {
                    skull.remove();
                    cancel();
                    return;
                }
                if (owner.isBlocking()) {
                    skull_direction = owner.getLocation().getDirection();
                }
                Location new_location = skull.getLocation().add(skull_direction.clone().multiply(1.6));
                new_location.setYaw((float) (Math.atan2(skull_direction.getZ(), skull_direction.getX()) / Math.PI * 180) - 90);
                skull.teleport(new_location);
                Utils.playParticle(EnumParticle.SMOKE_NORMAL, new_location.add(0, 2, 0),
                        0, 0, 0, 0.01f, 2, 96, skull.getWorld().getPlayers());
                boolean hit_entity = false;
                boolean hit_block = skull.getLocation().add(0, 1.5, 0).getBlock().getType().isSolid();
                if (!hit_block) {
                    for (LivingEntity livingEntity : Utils.getInRadius(new_location, direct_radius).keySet()) {
                        if(!(livingEntity instanceof Player)) {

                        }
                        if (livingEntity.equals(owner)) {
                            continue;
                        }
                        if (livingEntity.equals(skull)) {
                            continue;
                        }
                        hit_entity = true;
                        break;
                    }
                }
                if (!hit_entity && !hit_block) {
                    return;
                }
                cancel();
                Location location = skull.getLocation();
                double scale = 0.4 + 0.6 * Math.min(1, skull.getTicksLived() / 20d);
                if (hit_block) {
                    location.add(0, 2, 0);
                }
                HashMap<LivingEntity, Double> can_hit = Utils.getInRadius(new_location, splash_radius);
                for (LivingEntity livingEntity : can_hit.keySet()) {
                    if(!(livingEntity instanceof Player)) {
                        continue;
                    }
                    if (livingEntity.equals(owner)) {
                        continue;
                    }
                    double distance_scale = can_hit.get(livingEntity);
                    SmashDamageEvent smashDamageEvent = new SmashDamageEvent(livingEntity, owner, damage * scale * distance_scale);
                    smashDamageEvent.multiplyKnockback(knockback);
                    smashDamageEvent.setIgnoreDamageDelay(true);
                    smashDamageEvent.setReason(name);
                    smashDamageEvent.callEvent();
                }
                location.getWorld().playSound(location, Sound.EXPLODE, 2.5F, 0.4F);
                Utils.playParticle(EnumParticle.EXPLOSION_HUGE, location,
                        0, 0, 0, 0, 1, 96, skull.getWorld().getPlayers());
                skull.remove();
            }
        };
        runnable.runTaskTimer(plugin, 0L, 0L);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void stopArmorStandDamage(SmashDamageEvent e) {
        if(!(e.getDamagee() instanceof ArmorStand)) {
            return;
        }
        ArmorStand skull = (ArmorStand) e.getDamagee();
        List<MetadataValue> data = skull.getMetadata("Wither Skull");
        if(data.size() <= 0) {
            return;
        }
        e.setCancelled(true);
    }

}