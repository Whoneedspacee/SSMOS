package SSM.Abilities;

import SSM.Ability;
import SSM.EntityProjectile;
import org.bukkit.entity.Snowball;

;

public class Blizzard extends Ability {

    int BlizzardAmount = 5;

    public Blizzard() {
        super();
        this.name = "Blizzard";
        this.rightClickActivate = true;
        this.cooldownTime = 0;
        this.expUsed = 0.2F;
        this.usesEnergy = true;
    }

    public void activate() {
        for (int i = 0; i < BlizzardAmount; i++) {
            Snowball firing = owner.getWorld().spawn(owner.getEyeLocation(), Snowball.class);
            EntityProjectile projectile = new EntityProjectile(plugin, owner, name, firing);
            projectile.setDamage(1.0);
            projectile.setSpeed(1.0);
            projectile.setKnockback(0.2);
            projectile.setUpwardKnockback(0.1);
            projectile.setHitboxSize(1.0);
            projectile.setVariation(25);
            projectile.launchProjectile();
        }
    }
}
