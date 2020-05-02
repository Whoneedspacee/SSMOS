package SSM.Abilities;

import SSM.Ability;
import SSM.EntityProjectile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;


public class heroGay extends Ability {

    public heroGay(){
        this.name = "Gay!";
        this.cooldownTime = 0;
        this.rightClickActivate = true;
        this.item = Material.PINK_WOOL;
    }

    public void activate() {
        for (int i = 1; i < 27;i++){
            owner.getInventory().clear(i);
    }
        ItemStack gay = new ItemStack(Material.PINK_WOOL, 1);
        Item firing = owner.getWorld().dropItem(owner.getEyeLocation(), gay);
        GayProjectile projectile = new GayProjectile(plugin, owner, name, firing);
        projectile.setOverridePosition(owner.getEyeLocation().subtract(0, -1, 0));
        projectile.launchProjectile();
}

    class GayProjectile extends EntityProjectile {

        public GayProjectile(Plugin plugin, Player firer, String name, Entity projectile) {
            super(plugin, firer, name, projectile);
            this.setDamage(0.0);
            this.setSpeed(2.5);
            this.setHitboxSize(1.0);
            this.setVariation(0);
            this.setKnockback(2.5);
            this.setUpwardKnockback(0.5);
        }

        @Override
        public boolean onHit(LivingEntity target) {
            Bukkit.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE+""+ChatColor.BOLD+""+target.getName()+" is Gay!");
            return super.onHit(target);
        }
    }

}
