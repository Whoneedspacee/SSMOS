package SSM.Attributes;

import SSM.Events.SmashDamageEvent;
import SSM.GameManagers.BlockRestoreManager;
import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.NetherPigDisguise;
import SSM.GameManagers.Disguises.PigDisguise;
import SSM.GameManagers.KitManager;
import SSM.Kits.Kit;
import SSM.Utilities.BlocksUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class NetherPig extends Attribute {

//    private double range = 5;
//    private long duration_ms = 2000;
    private final double min_health = 6;
    private final double max_health = 10;


    public NetherPig() {
        super();
        this.name = "Nether Pig";
        this.usage = AbilityUsage.PASSIVE;
        this.description = new String[] {
                ChatColor.RESET + "When your health drops below 4, you morph",
                ChatColor.RESET + "into a Nether Pig. This gives you Speed I,",
                ChatColor.RESET + "8 Armor and reduces Energy costs by 33%.",
                ChatColor.RESET + "",
                ChatColor.RESET + "When your health returns to 6, you return",
                ChatColor.RESET + "back to Pig Form.",
        };
        // values in description are wrong? and repeatedly changed throughout mp code
        task = this.runTaskTimer(plugin, 0, 0);
    }

    @Override
    public void run() {
        checkAndActivate();
    }

    public void activate() {
        if (DisguiseManager.getDisguise(owner) instanceof NetherPigDisguise){
            owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 0, false, false));
        }
    }

    @EventHandler
    public void healthRegain(EntityRegainHealthEvent event){
        if (!event.getEntity().equals(owner)) return;
        changeForm();
    }

    @EventHandler
    public void damage(EntityDamageEvent event){
        if (!event.getEntity().equals(owner)) return;
        changeForm();
    }

    private void changeForm(){
        double health = owner.getHealth();
        Kit kit = KitManager.getPlayerKit(owner);

        if (health <= min_health && !(DisguiseManager.getDisguise(owner) instanceof NetherPigDisguise)){
            kit.setArmor(8);
            kit.setArmorSlot(Material.IRON_BOOTS, 0);
            kit.setArmorSlot(Material.IRON_LEGGINGS, 1);
            kit.setArmorSlot(Material.IRON_CHESTPLATE, 2);
            kit.setArmorSlot(Material.IRON_HELMET, 3);
            owner.getWorld().playSound(owner.getLocation(), Sound.ZOMBIE_PIG_ANGRY, 2f, 1f);
            owner.getWorld().playSound(owner.getLocation(), Sound.ZOMBIE_PIG_ANGRY, 2f, 1f);
            owner.setExp(0.99f);
            DisguiseManager.addDisguise(owner, new NetherPigDisguise(owner));
        }else if (health >= max_health && !(DisguiseManager.getDisguise(owner) instanceof PigDisguise)){
            owner.getWorld().playSound(owner.getLocation(), Sound.PIG_IDLE, 2f, 1f);
            owner.getWorld().playSound(owner.getLocation(), Sound.PIG_IDLE, 2f, 1f);
            kit.setArmor(5);
            kit.setArmorSlot(Material.CHAINMAIL_BOOTS, 0);
            kit.setArmorSlot(Material.CHAINMAIL_LEGGINGS, 1);
            kit.setArmorSlot(Material.CHAINMAIL_CHESTPLATE, 2);
            kit.setArmorSlot(Material.AIR, 3);
            DisguiseManager.addDisguise(owner, new PigDisguise(owner));

        }
    }


}
