package SSM.Utilities;

import SSM.GameManagers.KitManager;
import SSM.Kit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class DamageUtil {

    public static void dealDamage(Player damager, LivingEntity damagee, double damage, boolean setExpDamage, boolean expAdd) {
        if (damagee instanceof Player) {
            Player target = (Player) damagee;
            Kit targetKit = KitManager.getPlayerKit(target);
            double armor = (100 - (targetKit.getArmor() * 8)) / 100;
            target.damage(armor * damage);
        } else {
            damagee.damage(damage);
        }
        if (setExpDamage) {
            if (expAdd) {
                damager.setLevel(damager.getLevel() + (int) damage);
            } else {
                damager.setLevel((int) damage);
            }
        }
    }

    public static void dealDamage(LivingEntity damagee, double damage) {
        if (damagee instanceof Player) {
            Player player = (Player) damagee;
            Kit kit = KitManager.getPlayerKit(player);
            double armor = (100 - (kit.getArmor() * 8)) / 100;
            damagee.damage(armor * damage);
        } else {
            damagee.damage(damage);
        }


    }
}
