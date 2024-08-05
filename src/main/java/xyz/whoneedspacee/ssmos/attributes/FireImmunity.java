package xyz.whoneedspacee.ssmos.attributes;

public class FireImmunity extends Attribute {

    public FireImmunity() {
        super();
        this.name = "Fire Immunity";
        task = this.runTaskTimer(plugin, 0, 0);
    }

    @Override
    public void run() {
        checkAndActivate();
    }

    public void activate() {
        owner.setFireTicks(-20);
    }

}
