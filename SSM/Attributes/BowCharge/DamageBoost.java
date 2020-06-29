package SSM.Attributes.BowCharge;

import org.bukkit.entity.Arrow;

;

public class DamageBoost extends BowCharge {

    public DamageBoost(double delay, double rate, int maxCharge) {
        super(delay, rate, maxCharge);
        this.name = "Charge Bow Damage";
    }

    @Override
    public void firedBow(Arrow arrow) {
        arrow.setDamage(arrow.getDamage() + charge);
    }

}

