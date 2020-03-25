package SSM.Abilities;

import SSM.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class Blizzard extends Ability {

    int BlizzardAmount = 5;

    public Blizzard() {
        super();
        this.name = "Blizzard";
        this.cooldownTime = 0;
        this.rightClickActivate = true;
    }

    public void activate() {
        for (int i = 0; i < BlizzardAmount; i++) {
            Snowball firing = owner.getWorld().spawn(owner.getEyeLocation(), Snowball.class);
            EntityProjectile projectile = new EntityProjectile(plugin, owner, name, firing);
            projectile.setDamage(1.0);
            projectile.setSpeed(1.0);
            projectile.setKnockback(0.2);
            projectile.setUpwardKnockback(0.1);
            projectile.setHitboxSize(1.0);
            projectile.setVariation(25);
            projectile.launchProjectile();
        }
    }
}
