package SSM.Utilities;

import SSM.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class VelocityUtil {

    public static void addKnockback(Player owner, LivingEntity target, double amount, double ylimit){
        Vector enemy = target.getLocation().toVector();
        Vector player = owner.getLocation().toVector();
        if (!(target instanceof Player)){
            Vector init = enemy.subtract(player);
            Vector finalVel = init.normalize().multiply(amount);
            target.setVelocity(new Vector(finalVel.getX(), ylimit, finalVel.getZ()));
            return;
        }
        Player target1 = (Player)target;
        Kit kit = SSM.playerKit.get(target1.getUniqueId());
        double kbToApply = 0.5+((1 - (target.getHealth()/20)) * amount) * kit.getKnockback();
        Vector difference = enemy.subtract(player);
        Vector finalVel = difference.normalize().multiply(kbToApply);
        target.setVelocity(new Vector(finalVel.getX(), ylimit, finalVel.getZ()));


    }
}
