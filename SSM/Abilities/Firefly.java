package SSM.Abilities;

import SSM.Ability;

public class Firefly extends Ability {

    int x = 0;
    int stall = -1;
    int runn = -1;

    public Firefly() {
        super();
        this.name = "Firefly";
        this.cooldownTime = 8;
        this.rightClickActivate = true;
    }

    public void activate(){
        x = 0;
    }
}
