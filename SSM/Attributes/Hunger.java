package SSM.Attributes;

import SSM.Attribute;

public class Hunger extends Attribute {

    double hungerLoss;
    double delay;

    public Hunger(){
        super();
        this.name = "Hunger";
        task = this.runTaskTimer(plugin, 0, (long) delay*20);
    }

    @Override
    public void run(){
        owner.setFoodLevel(owner.getFoodLevel()-1);
    }
}
