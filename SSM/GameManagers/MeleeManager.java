package SSM.GameManagers;

import SSM.Kit;
import SSM.SSM;
import SSM.Utilities.DamageUtil;
import SSM.Utilities.VelocityUtil;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class MeleeManager implements Listener {

    private static MeleeManager ourInstance = new MeleeManager();

    public MeleeManager() {
        SSM.getInstance().getServer().getPluginManager().registerEvents(this, SSM.getInstance());
    }

    @EventHandler
    public void melee(EntityDamageByEntityEvent e) {
        Entity entity = e.getDamager();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            Kit playerKit = KitManager.getPlayerKit(player);
            e.setCancelled(true);
            DamageUtil.dealDamage((Player) e.getDamager(), (LivingEntity) e.getEntity(), playerKit.getMelee(), true, false);
            VelocityUtil.addKnockback((Player) e.getDamager(), (LivingEntity) e.getEntity(), 1.5, 0.3);
        }
    }
}
