package SSM;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

import java.util.UUID;

public class CustomPvP extends Kit implements Listener {

    @EventHandler
    public void onMelee(EntityDamageByEntityEvent e){
        LivingEntity victim = (LivingEntity) e.getEntity();
        LivingEntity attacker = (LivingEntity) e.getDamager();
        if (e.getDamager() instanceof Player){
            e.setCancelled(true);
            Vector target = e.getEntity().getLocation().toVector();
            Vector player = e.getDamager().getLocation().toVector();
            Vector pre = target.subtract(player);
            Vector velocity = pre.normalize().multiply(1.35);
            victim.damage(SSM.playerKit.get(attacker.getUniqueId()).damage);
            e.getEntity().setVelocity(new Vector(velocity.getX()*((1 - (victim.getHealth()/20)) * 1) * 1, 0.4, velocity.getZ()*((1 - (victim.getHealth()/20)) * 1) * 1));
            victim.setNoDamageTicks(10);
            victim.getServer().broadcastMessage("" + victim.getHealth());

        }
    }
}