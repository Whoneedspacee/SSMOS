package SSM.Attributes.BowCharge;

import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class DamageBoost extends BowCharge {

    private Arrow firedArrow;

    public DamageBoost(double delay, double rate, int maxCharge) {
        super(delay, rate, maxCharge);
        this.name = "Charge Bow Damage";
    }

    @Override
    public void firedBow(Arrow arrow) {
        firedArrow = arrow;
        checkAndActivate();
    }

    public void activate() {
        firedArrow.setMetadata("Damage Boost", new FixedMetadataValue(plugin, charge));
    }

    @EventHandler
    public void arrowDamage(EntityDamageByEntityEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            if (e.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) e.getDamager();
                List<MetadataValue> data = arrow.getMetadata("Damage Boost");
                if (data.size() > 0) {
                    int charge = data.get(0).asInt();
                    e.setDamage(e.getDamage() + charge);
                }
            }
        }
    }

}

