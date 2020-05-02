package SSM.Abilities;

import SSM.Ability;
import SSM.EntityProjectile;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class heroParalyze extends Ability {

    public heroParalyze(){
        this.name = "Paralyze!";
        this.cooldownTime = 0;
        this.rightClickActivate = true;
        this.item = Material.PUFFERFISH;
        this.expUsed = 0.2F;
        this.usesEnergy = true;
    }


    public void activate() {
        ItemStack gay = new ItemStack(Material.YELLOW_WOOL, 1);
        Item firing = owner.getWorld().dropItem(owner.getEyeLocation(), gay);
        ParalyzeProjectile projectile = new ParalyzeProjectile(plugin, owner, name, firing);
        projectile.setOverridePosition(owner.getEyeLocation().subtract(0, -1, 0));
        projectile.launchProjectile();
        for (int i = 1; i < 27;i++){
            owner.getInventory().clear(i);
        }

    }

    class ParalyzeProjectile extends EntityProjectile {

        public ParalyzeProjectile(Plugin plugin, Player firer, String name, Entity projectile) {
            super(plugin, firer, name, projectile);
            this.setDamage(3.0);
            this.setSpeed(1.5);
            this.setHitboxSize(1.0);
            this.setVariation(0);
            this.setKnockback(0.0);
            this.setUpwardKnockback(0.5);
        }

        @Override
        public boolean onHit(LivingEntity target) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 200));
            return super.onHit(target);
        }
}
}
