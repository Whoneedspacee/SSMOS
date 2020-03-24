package SSM.Abilities;

import SSM.*;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class SulphurBomb extends Ability {

    public SulphurBomb() {
        super();
        this.name = "Sulphur Bomb";
        this.cooldownTime = 3;
        this.rightClickActivate = true;
    }

    public void activate() {
        ItemStack coal = new ItemStack(Material.COAL);
        Item firing = owner.getWorld().dropItem(owner.getEyeLocation(), coal);
        EntityProjectile projectile = new EntityProjectile(plugin, owner, name, firing);
        projectile.setDamage(6.0);
        projectile.setSpeed(1.8);
        projectile.setKnockback(2.0);
        projectile.setUpwardKnockback(0.5);
        projectile.setHitboxSize(1.0);
        projectile.launchProjectile();
    }

}
