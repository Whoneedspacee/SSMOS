package SSM.Utilities;

import SSM.GameManagers.KitManager;
import SSM.Kit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class VelocityUtil {

    public static void addKnockback(Player owner, LivingEntity target, double amount, double ylimit) {
        Vector ownerLocation = owner.getLocation().toVector();
        Vector enemyLocation = target.getLocation().toVector();
        if (!(target instanceof Player)) {
            Vector init = enemyLocation.subtract(ownerLocation);
            Vector finalVel = init.normalize().multiply(amount);
            target.setVelocity(new Vector(finalVel.getX(), ylimit, finalVel.getZ()));
            return;
        }
        Player enemy = (Player) target;
        Kit kit = KitManager.getPlayerKit(enemy);
        double kbToApply = 0.5 + ((1 - (target.getHealth() / 20)) * amount) * kit.getKnockback();
        Vector difference = enemyLocation.subtract(ownerLocation);
        Vector finalVel = difference.normalize().multiply(kbToApply);
        target.setVelocity(new Vector(finalVel.getX(), ylimit, finalVel.getZ()));


    }
}
