package SSM.Abilities;

import SSM.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class MagmaBlast extends Ability {

    private LargeFireball largeFireball;

    public MagmaBlast() {
        super();
        this.name = "MagmaBlast";
        this.cooldownTime = 8;
        this.rightClickActivate = true;
    }

    public void activate() {
        largeFireball = owner.launchProjectile(LargeFireball.class);
        largeFireball.setCustomName("MagmaBlast");
        largeFireball.setVelocity(owner.getEyeLocation().getDirection().multiply(1.0D));
        Double x = owner.getPlayer().getEyeLocation().getDirection().getX() * -1.36;
        Double z = owner.getPlayer().getEyeLocation().getDirection().getZ() * -1.36;
        Double y = owner.getPlayer().getEyeLocation().getDirection().getY() * -1.36;
        owner.setVelocity(new Vector(x, y, z));
    }
}
