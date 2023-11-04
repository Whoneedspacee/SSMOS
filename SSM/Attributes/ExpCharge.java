package SSM.Attributes;

import SSM.Utilities.Utils;
import org.bukkit.entity.Player;

public class ExpCharge extends Attribute {

    protected float expAdd;
    protected double delay;
    protected boolean chargeWhenInAir;
    protected boolean chargeWhenSneaking;
    public boolean enabled = true;

    public ExpCharge(float expAdd, double delay, boolean chargeWhenInAir, boolean chargeWhenSneaking) {
        super();
        this.name = "Exp Charge";
        this.expAdd = expAdd;
        this.delay = delay;
        this.chargeWhenInAir = chargeWhenInAir;
        this.chargeWhenSneaking = chargeWhenSneaking;
        task = this.runTaskTimer(plugin, 0, (long) delay);
    }

    @Override
    public void run() {
        if (!chargeWhenInAir && !Utils.entityIsOnGround(owner)) {
            return;
        }
        if(!chargeWhenSneaking && owner.isSneaking()) {
            return;
        }
        checkAndActivate();
    }

    @Override
    public void setOwner(Player owner) {
        if (owner != null) {
            owner.setExp(1.0f - expAdd);
        }
        super.setOwner(owner);
    }

    public void activate() {
        if (enabled && !owner.isDead()) {
            float xp = owner.getExp();
            owner.setExp(Math.min(xp + expAdd, 1.0f));
        }
    }

}