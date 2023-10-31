package SSM.Abilities;

import SSM.Events.SmashDamageEvent;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Utilities.DamageUtil;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class BoneExplosion extends Ability implements OwnerRightClickEvent {

    private double range = 7;
    private double baseDamage = 6;

    public BoneExplosion() {
        super();
        this.name = "Bone Explosion";
        this.cooldownTime = 10;
        this.description = 	new String[] {
                ChatColor.RESET + "Releases an explosion of bones from",
                ChatColor.RESET + "your body, repelling all nearby enemies."};
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        Location location = owner.getLocation().add(0, 0.5, 0);
        owner.getWorld().playSound(owner.getLocation(), Sound.SKELETON_HURT, 2f, 1.2f);

        int entity_amount = 48;
        List<Entity> boneItems = new ArrayList<>();
        for (int i = 0; i < entity_amount; i++) {
            Item item = location.getWorld().dropItem(location, new ItemStack(Material.BONE, 1));
            item.setVelocity(new Vector((Math.random() - 0.5) * 0.8, Math.random() * 0.8, (Math.random() - 0.5) * 0.8));
            item.setPickupDelay(999999);
            boneItems.add(item);
        }

        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                for (Entity ent : boneItems) {
                    ent.remove();
                }
            }
        }, 40L);

        List<Entity> canHit = owner.getNearbyEntities(range, range, range);
        canHit.remove(owner);
        for (Entity entity : canHit) {
            if ((entity instanceof LivingEntity)) {
                if (!DamageUtil.canDamage((LivingEntity) entity, owner, baseDamage)) {
                    continue;
                }
                SmashDamageEvent smashDamageEvent = new SmashDamageEvent((LivingEntity) entity, owner, baseDamage);
                smashDamageEvent.multiplyKnockback(2.5);
                smashDamageEvent.setReason(name);
                smashDamageEvent.callEvent();
                Vector target = entity.getLocation().toVector();
                Vector player = owner.getLocation().toVector();
                Vector pre = target.subtract(player);
                Vector velocity = pre.normalize().multiply(1.35);
                entity.setVelocity(new Vector(velocity.getX(), 0.4, velocity.getZ()));
            }
        }
    }
}