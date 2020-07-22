package SSM.Attributes;

import SSM.Attribute;
import org.bukkit.GameMode;

public class Hunger extends Attribute {

    double delay;

    public Hunger(double delay) {
        super();
        this.name = "Hunger";
        task = this.runTaskTimer(plugin, 10, (long) delay * 20);
    }

    @Override
    public void run() {
        if (owner.getGameMode() != GameMode.CREATIVE)
            owner.setFoodLevel(owner.getFoodLevel() - 1);
    }

}
