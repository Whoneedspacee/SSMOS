package SSM.Kits;

import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Regeneration;
import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.ChickenDisguise;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class KitChicken extends Kit {

    public KitChicken() {
        super();
        this.damage = 4.5;
        this.armor = 2.5;
        this.regeneration = 0.25;
        this.knockback = 2.0;
        this.name = "Chicken";
        this.menuItem = Material.EGG;
    }

    public void equipKit(Player player) {
        super.equipKit(player);

        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);

        setItem(Material.IRON_SWORD, 0, null);
        setItem(Material.IRON_AXE, 1, null);

        addAttribute(new Regeneration(regeneration, 20));
        addAttribute(new GenericDoubleJump(0.9, 0.9, 1, Sound.GHAST_FIREBALL));

        DisguiseManager.addDisguise(owner, new ChickenDisguise(owner));
    }

}
