package ssm.attributes;

import ssm.utilities.Utils;
import org.bukkit.entity.Player;

public class ExpCharge extends Attribute {

    protected double delay;
    protected boolean chargeWhenInAir;
    protected boolean chargeWhenSneaking;
    protected boolean startFullEnergy;
    public boolean enabled = true;
    public float expAdd;

    public ExpCharge(float expAdd, double delay, boolean chargeWhenInAir, boolean chargeWhenSneaking, boolean startFullEnergy) {
        super();
        this.name = "Exp Charge";
        this.expAdd = expAdd;
        this.delay = delay;
        this.chargeWhenInAir = chargeWhenInAir;
        this.chargeWhenSneaking = chargeWhenSneaking;
        this.startFullEnergy = startFullEnergy;
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
        if (owner != null && startFullEnergy) {
            owner.setExp(1.0f);
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