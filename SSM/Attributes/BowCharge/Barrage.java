package SSM.Attributes.BowCharge;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;

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
        double initialVelocity = firedArrow.getVelocity().length();
        for (int i = 0; i < charge; i++) {
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    Arrow arrow = owner.launchProjectile(Arrow.class, owner.getLocation().getDirection().multiply(initialVelocity));
                    arrow.setCustomName("Barrage Arrow");
                    owner.playSound(owner.getLocation(), Sound.SHOOT_ARROW, 1.0F, 1.0F);
                }
            }, (i + 1) * 2);
        }
    }

}

