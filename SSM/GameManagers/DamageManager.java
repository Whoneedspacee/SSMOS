package SSM.GameManagers;

import org.bukkit.entity.Player;

public class DamageManager {

    public static double finalDamage(double damage, double armor){
        double multiplier = (100-(armor*8)) / 100;
        return damage*multiplier;
    }

}
