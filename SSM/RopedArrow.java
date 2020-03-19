package me.SirInHueman.SSM;



import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Listener;;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;


public class RopedArrow extends JavaPlugin implements Listener{


    public static void velocity(Player player, Projectile arrow) {
        Vector p = player.getLocation().toVector();
        Vector a = arrow.getLocation().toVector();
        Vector pre = a.subtract(p);
        Vector velocity = pre.normalize().multiply(1.8);

        player.setVelocity(new Vector(velocity.getX(), 1, velocity.getZ()));
        arrow.remove();


    }


    public static void ability(Player player) {
        Arrow arrow = player.launchProjectile(Arrow.class);
        arrow.setCustomName("Roped Arrow");
        arrow.setDamage(6.0);
        arrow.setVelocity(player.getLocation().getDirection().multiply(1.8D));


    }


    }











