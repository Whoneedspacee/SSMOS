package SSM.Kits;

import SSM.Abilities.Firefly;
import SSM.Abilities.MilkSpiral;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Potion;
import SSM.Attributes.Regeneration;
import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.BlazeDisguise;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class KitBlaze extends Kit {

    public KitBlaze() {
        super();
        this.damage = 6;
        this.armor = 6;
        this.regeneration = 0.25;
        this.knockback = 1.50;
        this.name = "Blaze";
        this.menuItem = Material.BLAZE_ROD;
    }

    public void equipKit(Player player) {
        super.equipKit(player);

        setArmor(Material.CHAINMAIL_BOOTS, 0);
        setArmor(Material.CHAINMAIL_LEGGINGS, 1);
        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);
        setArmor(Material.CHAINMAIL_HELMET, 3);

        setItem(Material.IRON_SWORD, 0, new MilkSpiral());
        setItem(Material.IRON_AXE, 1, new Firefly());

        addAttribute(new Regeneration(regeneration, 20));
        addAttribute(new GenericDoubleJump(1.0, 1.0, 1, Sound.GHAST_FIREBALL));
        addAttribute(new Potion(PotionEffectType.SPEED, 1));

        DisguiseManager.addDisguise(owner, new BlazeDisguise(owner));
    }

}