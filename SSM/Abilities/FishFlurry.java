package SSM.Abilities;

import SSM.EntityProjectile;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Utilities.VelocityUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class FishFlurry extends Ability implements OwnerRightClickEvent {

    protected int webAmount = 20;

    public FishFlurry() {
        super();
        this.name = "Spin Web";
        this.cooldownTime = 10;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        owner.getWorld().playSound(owner.getLocation(), Sound.SPIDER_IDLE, 2f, 0.6f);
        ItemStack cobweb = new ItemStack(Material.WEB, 1);
        for (int i = 0; i < webAmount; i++) {
            Item firing = owner.getWorld().dropItem(owner.getLocation().add(0, 0.5, 0), cobweb);
            WebProjectile projectile = new WebProjectile(plugin, name, firing);
            projectile.setFirer(owner);
            projectile.launchProjectile();
        }
        VelocityUtil.setVelocity(owner, 1.2, 0.2, 1.2, true);
    }

    class WebProjectile extends EntityProjectile {

        public WebProjectile(Plugin plugin, String name, Entity projectile) {
            super(plugin, name, projectile);
            this.setDamage(6.0);
            this.setHitboxSize(0.5);
            this.setTime(8);
            this.setTimed(true);
        }

        public List<Entity> hitDetection() {
            if (projectile.getLocation().getBlock().getType() == Material.WEB) {
                projectile.remove();
                return new ArrayList<Entity>();
            }
            return super.hitDetection();
        }

        @Override
        public void doVelocity() {
            Vector spread = new Vector(Math.random(), Math.random(), Math.random()).subtract(new Vector(0.5, 0.5, 0.5));
            spread.normalize();
            spread.multiply(0.2);
            VelocityUtil.setVelocity(projectile, owner.getLocation().getDirection().multiply(-1).add(spread),
                    Math.random() * 0.4 + 1, false, 0, 0.2, 10, false);
        }

        @Override
        public void doKnockback(LivingEntity target) {
            Block replace = target.getLocation().getBlock();
            Material replacedType = replace.getType();
            target.setVelocity(new Vector(0, 0, 0));
            if (replacedType == Material.WEB) {
                return;
            }
            replace.setType(Material.WEB);
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    replace.setType(replacedType);
                }
            }, (long) 3 * 20);
        }

        @Override
        public void onBlockHit() {
            Block replace = projectile.getLocation().getBlock();
            Material replacedType = replace.getType();
            if (replacedType == Material.WEB) {
                return;
            }
            replace.setType(Material.WEB);
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    replace.setType(replacedType);
                }
            }, (long) 2 * 20);
        }

    }

}
