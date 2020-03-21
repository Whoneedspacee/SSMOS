package SSM.Abilities;

import SSM.*;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class IronHook extends Ability {

    public IronHook(Plugin plugin) {
        super(plugin);
        this.name = "Iron Hook";
        this.cooldownTime = 8;
        this.rightClickActivate = true;
    }

    public void useAbility(Player player) {
        ItemStack hook = new ItemStack(Material.TRIPWIRE_HOOK);
        Item firing = player.getWorld().dropItem(player.getEyeLocation(), hook);
        EntityProjectile projectile = new EntityProjectile(plugin, player, name, firing);
        projectile.setDamage(5.0);
        projectile.setSpeed(1.8);
        projectile.setKnockback(-2.0);
        projectile.setHitboxSize(1.0);
        projectile.launchProjectile();
    }

}
