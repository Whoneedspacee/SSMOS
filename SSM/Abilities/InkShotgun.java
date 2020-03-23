package SSM.Abilities;

import SSM.Ability;
import SSM.EntityProjectile;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class InkShotgun extends Ability {

    protected int inkAmount = 7;

    public InkShotgun(Plugin plugin) {
        super(plugin);
        this.name = "Super Squid";
        this.cooldownTime = 6;
        this.rightClickActivate = true;
    }

    public void useAbility(Player player) {
        ItemStack ink = new ItemStack(Material.INK_SAC, inkAmount);
        for (int i = 0; i < inkAmount; i++) {
            Item firing = player.getWorld().dropItem(player.getEyeLocation(), ink);
            InkProjectile projectile = new InkProjectile(plugin, player, name, firing);
            projectile.setOverridePosition(player.getEyeLocation().subtract(0, -1, 0));
            projectile.launchProjectile();
        }
    }

    class InkProjectile extends EntityProjectile {

        protected double vanishTime = 10;

        public InkProjectile(Plugin plugin, Player firer, String name, Entity projectile) {
            super(plugin, firer, name, projectile);
            this.setDamage(1.5);
            this.setSpeed(1.8);
            this.setHitboxSize(1.0);
            this.setVariation(30);
            this.setFireOpposite(false);

    }
}
}




