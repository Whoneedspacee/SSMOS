package SSM.Abilities;

import SSM.Events.SmashDamageEvent;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Utilities.DamageUtil;
import SSM.Utilities.ServerMessageType;
import SSM.Utilities.Utils;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BoneExplosion extends Ability implements OwnerRightClickEvent {

    private double range = 7;
    private double baseDamage = 6;

    public BoneExplosion() {
        super();
        this.name = "Bone Explosion";
        this.cooldownTime = 10;
        this.description = new String[] {
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

        HashMap<LivingEntity, Double> canHit = Utils.getInRadius(owner.getLocation(), range);
        canHit.remove(owner);
        for (LivingEntity other : canHit.keySet()) {
            if ((other instanceof Player)) {
                Player hit = (Player) other;
                double damage = baseDamage * canHit.get(hit);
                if (!DamageUtil.canDamage(hit, owner, damage)) {
                    continue;
                }
                SmashDamageEvent smashDamageEvent = new SmashDamageEvent(hit, owner, damage);
                smashDamageEvent.multiplyKnockback(2.5);
                smashDamageEvent.setIgnoreDamageDelay(true);
                smashDamageEvent.setReason(name);
                smashDamageEvent.callEvent();
                Utils.sendAttributeMessage(ChatColor.YELLOW + owner.getName() +
                        ChatColor.GRAY + " used", name, hit, ServerMessageType.GAME);
            }
        }
    }
}