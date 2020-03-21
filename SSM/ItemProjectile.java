package SSM;

import SSM.Kits.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ItemProjectile extends BukkitRunnable {

    Player firer;
    double damage;
    double speed;
    double knockBack;
    double hitboxRange;
    int amount;
    boolean ylimit;
    boolean clearOnFinish;
    Item projectile;

    public ItemProjectile(Plugin plugin, Player firer, String name, Material item, double damage, double speed, double knockBack, int amount, double hitboxRange, double variation, boolean clearOnFinish, boolean ylimit) {
        this.firer = firer;
        this.damage = damage;
        this.speed = speed;
        this.knockBack = knockBack;
        this.hitboxRange = hitboxRange;
        this.clearOnFinish = clearOnFinish;
        this.amount = amount;
        this.ylimit = ylimit;
        for (int i = 0; i < amount; i++){
            launchProjectile(name, item, variation);
            // ]]The reason I don't just set the amount when you create the ItemStack is because if we try to
            // create something with an offset (Like spin web or needler), I think it would apply that velocity offset
            // to the entire stack instead of just one item (And potentially delay? Like egg blaster and needler).
        }
        this.runTaskTimer(plugin, 0L, 1L);
    }

    public void launchProjectile(String name, Material item, double variation) {
        ItemStack projectileStack = new ItemStack(item, 1);
        projectile = firer.getWorld().dropItem(firer.getEyeLocation(), projectileStack);
        projectile.setCustomName(name);
        projectile.setPickupDelay(1000000);
        projectile.setVelocity(firer.getLocation().getDirection().multiply(speed));
    }

    @Override
    public void run() {
        if (projectile.isDead() || !projectile.isDead() && projectile.isOnGround()) {
            onHit(null);
            this.cancel();
            return;
        }
        List<Entity> canHit = projectile.getNearbyEntities(hitboxRange, hitboxRange, hitboxRange);
        canHit.remove(projectile);
        canHit.remove(firer);
        if (canHit.size() <= 0) {
            return;
        }
        for (Entity entity : canHit) {
            if (!(entity instanceof LivingEntity)) {
                continue;
            }
            LivingEntity target = (LivingEntity) canHit.get(0);
            onHit(target);
            this.cancel();
            break;
        }
    }

    public boolean onHit(LivingEntity target) {
        boolean success = target != null;
        if (success) {
            target.damage(damage);
            Vector velocity = projectile.getVelocity().normalize().multiply(knockBack);
            if (ylimit = true){
                target.setVelocity(new Vector(velocity.getX(), 0.5, velocity.getZ()));
            }else{
                target.setVelocity(velocity);
            }
        }
        if (clearOnFinish) {
            projectile.remove();
        }
        return success;
    }

}












