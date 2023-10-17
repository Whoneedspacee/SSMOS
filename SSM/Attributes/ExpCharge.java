package SSM.Attributes;

import SSM.Utilities.Utils;
import org.bukkit.entity.Player;

public class ExpCharge extends Attribute {

    protected float expAdd;
    protected double delay;
    protected boolean groundCharge;

    public ExpCharge(float expAdd, double delay, boolean groundCharge) {
        super();
        this.name = "ExpCharge";
        this.expAdd = expAdd;
        this.delay = delay;
        this.groundCharge = groundCharge;
        task = this.runTaskTimer(plugin, 0, (long) delay);
    }

    @Override
    public void run() {
        if (groundCharge && !Utils.entityIsOnGround(owner)) {
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
        if (!owner.isDead()) {
            float xp = owner.getExp();
            owner.setExp(Math.min(xp + expAdd, 1.0f));
        }
    }

}