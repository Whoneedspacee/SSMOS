package SSM.Attributes;

import SSM.Attribute;
import SSM.GameManagers.KitManager;
import SSM.GameManagers.OwnerEvents.OwnerDealDamageEvent;
import SSM.Kit;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Stampede extends Attribute implements OwnerDealDamageEvent {

    private int ticksRunning = 0;
    private int timePerLevel; // seconds
    private int levelAmount;
    private int level = 1;

    public Stampede(int timePerLevel, int levelAmount) {
        super();
        this.name = "Stampede";
        this.timePerLevel = timePerLevel;
        this.levelAmount = levelAmount;
        task = this.runTaskTimer(plugin, 0, 1L);
    }

    @Override
    public void run() {
        Kit kit = KitManager.getPlayerKit(owner);
        if (owner.isSprinting()) {
            ticksRunning++;
        } else {
            ticksRunning = 0;
            kit.setMelee(kit.getMelee() - (level - 1));
            level = 1;
        }
        if (ticksRunning / 20 >= timePerLevel * level && ticksRunning / 20 < timePerLevel * (level + 1) && level <= levelAmount) {
            owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, level - 1));
            level++;
            kit.setMelee(kit.getMelee() + 1);
        } else if (ticksRunning / 20 >= (timePerLevel * (level - 1)) && level > 1) {
            owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, level - 2));
        }
    }

    public void onOwnerDealDamage(EntityDamageByEntityEvent e) {
        if (level == 1) {
            return;
        }
        Kit kit = KitManager.getPlayerKit(owner);
        ticksRunning = 0;
        kit.setMelee(kit.getMelee() - (level - 1));
        level = 1;
        owner.setSprinting(false);
    }

}
