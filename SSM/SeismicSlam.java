package me.SirInHueman.SSM;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class SeismicSlam {
    public static void ability(Player player){
        Double x = player.getLocation().getDirection().getX() * 0.9;
        Double z = player.getLocation().getDirection().getZ() * 0.9;

        player.setVelocity(new Vector(x, 0.42, z));

    }
}
