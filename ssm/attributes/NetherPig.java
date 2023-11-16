package ssm.attributes;

import ssm.gamemanagers.DisguiseManager;
import ssm.gamemanagers.disguises.NetherPigDisguise;
import ssm.gamemanagers.disguises.PigDisguise;
import ssm.gamemanagers.KitManager;
import ssm.kits.Kit;
import ssm.utilities.ServerMessageType;
import ssm.utilities.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NetherPig extends Attribute {

    protected double transform_zombie_health = 6;
    protected double transform_normal_health = 10;
    public boolean active = false;

    public NetherPig() {
        super();
        this.name = "Nether Pig";
        this.usage = AbilityUsage.PASSIVE;
        this.description = new String[]{
                ChatColor.RESET + "When your health drops below 4, you morph",
                ChatColor.RESET + "into a Nether Pig. This gives you Speed I,",
                ChatColor.RESET + "8 Armor and reduces Energy costs by 33%.",
                ChatColor.RESET + "",
                ChatColor.RESET + "When your health returns to 6, you return",
                ChatColor.RESET + "back to Pig Form.",
        };
        task = this.runTaskTimer(plugin, 0, 5);
    }

    @Override
    public void run() {
        checkAndActivate();
    }

    public void activate() {
        Kit kit = KitManager.getPlayerKit(owner);
        if (active) {
            owner.removePotionEffect(PotionEffectType.SPEED);
            owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 18, 0, false, false));
            if(owner.getHealth() < transform_normal_health) {
                return;
            }
            active = false;
            kit.setArmor(5);
            kit.setArmorSlot(Material.CHAINMAIL_BOOTS, 0);
            kit.setArmorSlot(Material.CHAINMAIL_LEGGINGS, 1);
            kit.setArmorSlot(Material.CHAINMAIL_CHESTPLATE, 2);
            owner.getInventory().setHelmet(null);
            owner.getWorld().playSound(owner.getLocation(), Sound.PIG_IDLE, 2f, 1f);
            owner.getWorld().playSound(owner.getLocation(), Sound.PIG_IDLE, 2f, 1f);
            DisguiseManager.addDisguise(owner, new PigDisguise(owner));
            Utils.sendAttributeMessage("You returned to",
                    ChatColor.GREEN + "Pig Form", owner, ServerMessageType.SKILL);
        } else {
            if(owner.getHealth() > transform_zombie_health) {
                return;
            }
            active = true;
            kit.setArmor(7.5);
            kit.setArmorSlot(Material.IRON_BOOTS, 0);
            kit.setArmorSlot(Material.IRON_LEGGINGS, 1);
            kit.setArmorSlot(Material.IRON_CHESTPLATE, 2);
            kit.setArmorSlot(Material.IRON_HELMET, 3);
            owner.getWorld().playSound(owner.getLocation(), Sound.ZOMBIE_PIG_ANGRY, 2f, 1f);
            owner.getWorld().playSound(owner.getLocation(), Sound.ZOMBIE_PIG_ANGRY, 2f, 1f);
            owner.setExp(0.99f);
            DisguiseManager.addDisguise(owner, new NetherPigDisguise(owner));
            Utils.sendAttributeMessage("You transformed into",
                    ChatColor.GREEN + "Nether Pig Form", owner, ServerMessageType.SKILL);
        }
    }

}
