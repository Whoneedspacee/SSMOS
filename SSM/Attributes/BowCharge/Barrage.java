package SSM.Attributes.BowCharge;

import SSM.GameManagers.KitManager;
import SSM.Kits.KitSkeleton;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import java.util.List;

public class Barrage extends BowCharge {

    private Arrow firedArrow;

    public Barrage(double delay, double rate, int maxCharge) {
        super(delay, rate, maxCharge);
        this.name = "Barrage";
    }

    public void firedBow(Arrow arrow) {
        firedArrow = arrow;
        checkAndActivate();
    }

    public void activate() {
        for (int i = 0; i < charge; i++) {
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    Vector spread = new Vector((Math.random() - 0.5) / 10, (Math.random() - 0.5) / 10, (Math.random() - 0.5) / 10);
                    Arrow arrow = owner.launchProjectile(Arrow.class);
                    arrow.setVelocity(owner.getLocation().getDirection().add(spread).multiply(3));
                    arrow.setMetadata("Barrage Damage", new FixedMetadataValue(plugin, 5));
                    owner.playSound(owner.getLocation(), Sound.SHOOT_ARROW, 1.0F, 1.0F);
                }
            }, (i + 1) * 2);
        }
    }

    @EventHandler
    public void arrowDamage(EntityDamageByEntityEvent e) {
        if(e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            if(e.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) e.getDamager();
                List<MetadataValue> data = arrow.getMetadata("Barrage Damage");
                if(data.size() > 0) {
                    int damage = data.get(0).asInt();
                    e.setDamage(damage);
                }
                else if (arrow.getShooter() instanceof Player) {
                    Player player = (Player) arrow.getShooter();
                    if(KitManager.getPlayerKit(player) instanceof KitSkeleton) {
                        e.setDamage(6.0);
                    }
                }
            }
        }
    }

}

