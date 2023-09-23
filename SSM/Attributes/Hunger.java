package SSM.Attributes;

import org.bukkit.GameMode;

public class Hunger extends Attribute {

    protected double delay;

    public Hunger(double delay) {
        super();
        this.name = "Hunger";
        task = this.runTaskTimer(plugin, 10, (long) delay * 20);
    }

    @Override
    public void run() {
        checkAndActivate();
    }

    public void activate() {
        if (owner.getGameMode() != GameMode.CREATIVE) {
            owner.setFoodLevel(owner.getFoodLevel() - 1);
        }
    }

}
