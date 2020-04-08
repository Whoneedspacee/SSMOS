package SSM.Attributes;

import SSM.Attribute;

public class ExpCharge extends Attribute {

    float expAdd;
    double delay;
    float xp;

    public ExpCharge(float expAdd, double delay) {
        super();
        this.name = "ExpCharge";
        this.expAdd = expAdd;
        this.delay = delay;
        task = this.runTaskTimer(plugin, 0, (long) delay);
    }

    @Override
    public void run() {

        if (!owner.isDead()) {
            xp = (owner.getExpToLevel()* expAdd)/owner.getExpToLevel();
            owner.setExp(owner.getExp()+(xp));
        }
    }
}