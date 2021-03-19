package SSM.Attributes;

import SSM.Attribute;
import SSM.Utilities.Utils;

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
        if(groundCharge && !Utils.playerIsOnGround(owner)) {
            return;
        }
        checkAndActivate();
    }

    public void activate() {
        if (!owner.isDead()) {
            float xp = owner.getExp();
            owner.setExp(Math.min(xp + expAdd, 1f));
        }
    }

}