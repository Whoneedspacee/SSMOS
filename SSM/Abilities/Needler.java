package SSM.Abilities;

import SSM.Ability;
import SSM.EntityProjectile;
import org.bukkit.entity.Arrow;

public class Needler extends Ability {

    int needleAmount = 6;

    public Needler() {
        super();
        this.name = "Needler";
        this.cooldownTime = 0;
        this.holdClickActivate = true;
    }

    public void activate() {
        while (owner.isBlocking()) {
            for (int i = 0; i < needleAmount; i++) {
                Arrow firing = owner.getWorld().spawn(owner.getEyeLocation(), Arrow.class);
                EntityProjectile projectile = new EntityProjectile(plugin, owner, name, firing);
                projectile.setDamage(1.0);
                projectile.setSpeed(1.0);
                projectile.setKnockback(0.4);
                projectile.setUpwardKnockback(0.2);
                projectile.setHitboxSize(0.5);
                projectile.setVariation(18);
                projectile.launchProjectile();
            }
        }
    }
}
