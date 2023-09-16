package SSM.Abilities;

import SSM.Ability;
import SSM.EntityProjectile;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class SpinWeb extends Ability implements OwnerRightClickEvent {

    protected int webAmount = 20;

    public SpinWeb() {
        super();
        this.name = "Spin Web";
        this.cooldownTime = 5;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        owner.getWorld().playSound(owner.getLocation(), Sound.SPIDER_DEATH, 10L, 1L);
        ItemStack cobweb = new ItemStack(Material.WEB, 1);
        for (int i = 0; i < webAmount; i++) {
            Item firing = owner.getWorld().dropItem(owner.getEyeLocation(), cobweb);
            WebProjectile projectile = new WebProjectile(plugin, owner.getEyeLocation().subtract(0, -1, 0), name, firing);
            projectile.setFirer(owner);
            projectile.launchProjectile();
        }
        owner.setVelocity(owner.getLocation().getDirection().multiply(1.5));
    }

    class WebProjectile extends EntityProjectile {

        protected double vanishTime = 4;

        public WebProjectile(Plugin plugin, Location fireLocation, String name, Entity projectile) {
            super(plugin, fireLocation, name, projectile);
            this.setDamage(6.0);
            this.setSpeed(1.8);
            this.setHitboxSize(1.0);
            this.setSpread(30);
            this.setExpAdd(true);
            this.setFireOpposite(true);
        }

        @Override
        public boolean onHit(LivingEntity target) {
            Block replace = projectile.getLocation().getBlock();
            Material replacedType = replace.getType();
            if (replacedType == Material.WEB) {
                return super.onHit(target);
            }
            replace.setType(Material.WEB);
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    replace.setType(replacedType);
                }
            }, (long) vanishTime * 20);
            return super.onHit(target);
        }

    }

}
