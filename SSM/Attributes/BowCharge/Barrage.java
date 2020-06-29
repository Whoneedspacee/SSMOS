package SSM.Attributes.BowCharge;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;

;

public class Barrage extends BowCharge {

    public Barrage(double delay, double rate, int maxCharge) {
        super(delay, rate, maxCharge);
        this.name = "Barrage";
    }

    public void firedBow(Arrow p) {
        double initialVelocity = p.getVelocity().length();
        for (int i = 0; i < charge; i++) {
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    Arrow arrow = owner.launchProjectile(Arrow.class, owner.getLocation().getDirection().multiply(initialVelocity));
                    arrow.setCustomName("Barrage Arrow");
                    owner.playSound(owner.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0F, 1.0F);
                }
            }, (i + 1) * 2);
        }
    }

}

