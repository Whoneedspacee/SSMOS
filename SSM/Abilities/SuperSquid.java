package SSM.Abilities;

import SSM.Ability;

public class SuperSquid extends Ability {

    public SuperSquid(){
        this.holdDownActivate = true;
        this.name = "Super Squid";
        this.cooldownTime = 0;
    }

    public void activate(){
        owner.setInvulnerable(true);
        owner.setVelocity(owner.getLocation().getDirection());
    }
}
