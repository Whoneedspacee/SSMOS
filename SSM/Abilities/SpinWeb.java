package SSM.Abilities;

import SSM.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class SpinWeb extends Ability {

    protected int webAmount = 20;

    public SpinWeb(Plugin plugin) {
        super(plugin);
        this.name = "Spin Web";
        this.cooldownTime = 5;
        this.rightClickActivate = true;
    }

    public void useAbility(Player player) {
        ItemStack cobweb = new ItemStack(Material.COBWEB, webAmount);
        for (int i = 0; i < webAmount; i++) {
            Item firing = player.getWorld().dropItem(player.getEyeLocation(), cobweb);
            WebProjectile projectile = new WebProjectile(plugin, player, name, firing);
            projectile.setOverridePosition(player.getEyeLocation().subtract(0, -1, 0));
            projectile.launchProjectile();
        }
        player.setVelocity(player.getLocation().getDirection().multiply(1.5));
    }

    class WebProjectile extends EntityProjectile {

        protected double vanishTime = 4;

        public WebProjectile(Plugin plugin, Player firer, String name, Entity projectile) {
            super(plugin, firer, name, projectile);
            this.setDamage(5.0);
            this.setSpeed(1.8);
            this.setHitboxSize(1.0);
            this.setVariation(30);
            this.setFireOpposite(true);
        }

        @Override
        public boolean onHit(LivingEntity target) {
            Block replace = projectile.getLocation().getBlock();
            Material replacedType = replace.getType();
            if (replacedType == Material.COBWEB) {
                return super.onHit(target);
            }
            replace.setType(Material.COBWEB);
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
