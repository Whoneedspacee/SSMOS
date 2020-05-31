package SSM.Attributes;

import SSM.Attribute;

public class Fatigue extends Attribute {

    double damage;

    public Fatigue(){
        super();
        this.name = "Fatigue";
        task = this.runTaskTimer(plugin, 0, 0);
    }

    @Override
    public void run(){
        if (owner.getFoodLevel() <= 6){
            owner.setSprinting(false);
        }
    }
}
