package SSM.Kits;

import SSM.Abilities.BoneExplosion;
import SSM.Abilities.RopedArrow;
import SSM.Attributes.BowCharge.Barrage;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.ItemGenerator;
import SSM.Attributes.Regeneration;
import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.EndermanDisguise;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class KitEnderman extends Kit {

    public KitEnderman() {
        super();
        this.damage = 7;
        this.armor = 6;
        this.regeneration = 0.25;
        this.knockback = 1.25;
        this.name = "Enderman";
        this.menuItem = Material.ENDER_PEARL;
    }

    public void equipKit(Player player) {
        super.equipKit(player);

        setArmor(Material.CHAINMAIL_BOOTS, 0);
        setArmor(Material.CHAINMAIL_LEGGINGS, 1);
        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);
        setArmor(Material.CHAINMAIL_HELMET, 3);

        setItem(Material.IRON_SWORD, 0, null);
        setItem(Material.IRON_AXE, 1, null);

        addAttribute(new Regeneration(regeneration, 20));
        addAttribute(new GenericDoubleJump(0.9, 0.9, 1, Sound.GHAST_FIREBALL));

        DisguiseManager.addDisguise(owner, new EndermanDisguise(owner));
    }

}
