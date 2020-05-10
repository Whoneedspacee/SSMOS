package SSM.Attributes;

import SSM.*;
import SSM.Utilities.DamageUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;


public class Stampede extends Attribute {

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
        Kit kit = SSM.playerKit.get(owner.getUniqueId());
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

    @EventHandler
    public void endStampede(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() == owner)) {
            return;
        }
        if (level == 1) {
            return;
        }
        Kit kit = SSM.playerKit.get(owner.getUniqueId());
        ticksRunning = 0;
        kit.setMelee(kit.getMelee() - (level - 1));
        level = 1;
        owner.setSprinting(false);
    }
}
