package SSM.Abilities;

import SSM.Events.SmashDamageEvent;
import SSM.GameManagers.CooldownManager;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Projectiles.IronHookProjectile;
import SSM.Utilities.DamageUtil;
import SSM.Utilities.ServerMessageType;
import SSM.Utilities.Utils;
import SSM.Utilities.VelocityUtil;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EntitySkeleton;
import net.minecraft.server.v1_8_R3.EntitySquid;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSkeleton;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSquid;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class WitherImage extends Ability implements OwnerRightClickEvent {

    private int image_task = -1;
    private int swap_delay_task = -1;
    private boolean can_swap = true;
    protected long swap_cooldown_ticks = 40;
    protected double target_radius = 15;
    protected Skeleton wither_image = null;

    public WitherImage() {
        super();
        this.name = "Wither Image";
        this.cooldownTime = 12;
        this.description = new String[] {
                ChatColor.RESET + "Create an exact image of yourself.",
                ChatColor.RESET + "The copy is launched forwards with",
                ChatColor.RESET + "high speeds. Lasts 8 seconds.",
                ChatColor.RESET + "",
                ChatColor.RESET + "Use the skill again to swap positions",
                ChatColor.RESET + "with your image.",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        if(Bukkit.getScheduler().isQueued(image_task) || Bukkit.getScheduler().isCurrentlyRunning(image_task)) {
            if (!can_swap) {
                return;
            }
            Utils.sendAttributeMessage("You used", "Wither Swap",
                    owner, ServerMessageType.SKILL);
            setSwapDelay(swap_cooldown_ticks);
            Vector old_image_vector = wither_image.getVelocity().clone();
            Vector old_owner_vector = owner.getVelocity().clone();
            Location wither_old_location = wither_image.getLocation().clone();
            Location wither_new_location = owner.getLocation().clone();
            // Cannot normally teleport entities with passengers
            EntitySkeleton nms_wither_image = ((CraftSkeleton) wither_image).getHandle();
            nms_wither_image.setPositionRotation(wither_new_location.getX(), wither_new_location.getY(), wither_new_location.getZ(),
                    wither_new_location.getYaw(), wither_new_location.getPitch());
            VelocityUtil.setVelocity(wither_image, old_owner_vector);
            // Teleporting adds no damage ticks?
            owner.teleport(wither_old_location);
            owner.setVelocity(old_image_vector);
            owner.getWorld().playSound(owner.getLocation(), Sound.WITHER_SPAWN, 1f, 2f);
            return;
        }
        checkAndActivate();
    }

    public void activate() {
        wither_image = (Skeleton) owner.getWorld().spawnEntity(owner.getEyeLocation(), EntityType.SKELETON);
        wither_image.setSkeletonType(Skeleton.SkeletonType.WITHER);
        wither_image.getEquipment().setItemInHand(owner.getItemInHand());
        wither_image.setMaxHealth(20);
        wither_image.setHealth(wither_image.getMaxHealth());
        getNewTarget();
        wither_image.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000, 1, false, false));
        // Custom Nametag Shenanigans (similar to our disguises)
        // Needed since 1.8.8 mobs do not show their custom name unless hovered over
        Utils.attachCustomName(wither_image, ChatColor.YELLOW + owner.getName());
        VelocityUtil.setVelocity(wither_image, owner.getLocation().getDirection(),
                1.6, false, 0, 0.2, 10, true);
        setSwapDelay(swap_cooldown_ticks / 4);
        owner.getWorld().playSound(owner.getLocation(), Sound.WITHER_SPAWN, 1f, 1f);
        // Update every 10 ticks
        image_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(owner == null || !owner.isValid() || !wither_image.isValid() ||
                        wither_image.getTicksLived() > 160 || wither_image.getLocation().getBlock().isLiquid()) {
                    Utils.itemEffect(wither_image.getLocation().add(0, 0.5, 0), 12, 0.3,
                            Sound.WITHER_HURT, 1f, 0.75f, Material.BONE, (byte) 0, 40);
                    wither_image.remove();
                    wither_image = null;
                    Bukkit.getScheduler().cancelTask(image_task);
                }
            }
        }, 0L, 10L);
    }

    public void getNewTarget() {
        for(Player player : Utils.getNearby(wither_image.getLocation(), target_radius)) {
            if(player.equals(owner)) {
                continue;
            }
            wither_image.setTarget(player);
            break;
        }
    }

    public void setSwapDelay(long ticks) {
        can_swap = false;
        if (Bukkit.getScheduler().isQueued(swap_delay_task) || Bukkit.getScheduler().isCurrentlyRunning(swap_delay_task)) {
            Bukkit.getScheduler().cancelTask(swap_delay_task);
        }
        swap_delay_task = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                Utils.sendAttributeMessage("You can use", "Wither Swap",
                        owner, ServerMessageType.RECHARGE);
                can_swap = true;
            }
        }, ticks);
    }

    @EventHandler
    public void entityTarget(EntityTargetEvent e) {
        if(wither_image == null) {
            return;
        }
        if(!(e.getEntity() instanceof Skeleton)) {
            return;
        }
        if(!e.getEntity().equals(wither_image)) {
            return;
        }
        if(e.getTarget() == null) {
            return;
        }
        if(e.getTarget().equals(owner)) {
            e.setCancelled(true);
        }
        if(!(e.getTarget() instanceof LivingEntity)) {
            return;
        }
        if(!DamageUtil.canDamage((LivingEntity) e.getTarget(), owner, 0)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void checkImageDamage(SmashDamageEvent e) {
        if(e.getDamager() == null || wither_image == null) {
            return;
        }
        if(!e.getDamager().equals(wither_image)) {
            return;
        }
        if(e.getDamagee().equals(owner)) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void transferImageDamage(SmashDamageEvent e) {
        if(e.getDamager() == null || wither_image == null) {
            return;
        }
        if(!e.getDamager().equals(wither_image)) {
            return;
        }
        e.setDamager(owner);
        e.setKnockbackOrigin(e.getDamager().getLocation());
        // Does 10.5 damage on hard mode with the axe, subtract 5.5 to get 5 damage
        e.setDamage(5);
    }

    @EventHandler
    public void onImageCombust(EntityCombustEvent e) {
        if(wither_image == null || !e.getEntity().equals(wither_image)) {
            return;
        }
        if(e instanceof EntityCombustByBlockEvent || e instanceof EntityCombustByEntityEvent) {
            return;
        }
        e.setCancelled(true);
    }

}