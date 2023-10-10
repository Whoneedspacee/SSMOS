package SSM.Attributes;

public class Regeneration extends Attribute {

    Double regen;
    double delay;

    public Regeneration(Double regen, long delay) {
        super();
        this.name = "Regeneration";
        this.regen = regen;
        this.delay = delay;
        task = this.runTaskTimer(plugin, 0, delay);
    }

    @Override
    public void run() {
        checkAndActivate();
    }

    public void activate() {
        if (!owner.isDead()) {
            owner.setHealth(Math.min(owner.getHealth() + regen, 20));
        }
    }

}
