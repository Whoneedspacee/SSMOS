package SSM.Attributes;

import SSM.*;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;;
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
        if (owner.isSprinting()){
            ticksRunning++;
        }else{
            ticksRunning = 0;
            level = 1;
        }

        if (ticksRunning/20 >= timePerLevel*level && ticksRunning/20 < timePerLevel*(level+1) && level <= levelAmount) {
            owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, level - 1));
            level++;

        }else if (ticksRunning/20 >= (timePerLevel*(level-1)) && level > 1){
            owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20,  level-2));
        }







    }
}
