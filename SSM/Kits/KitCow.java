package SSM.Kits;

import SSM.Abilities.InkShotgun;
import SSM.Abilities.MilkSpiral;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Regeneration;
import SSM.Attributes.Stampede;
import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.CowDisguise;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class KitCow extends Kit {

    public KitCow() {
        super();
        this.damage = 6;
        this.armor = 7;
        this.regeneration = 0.25;
        this.knockback = 1.1;
        this.name = "Cow";
        this.menuItem = Material.RAW_BEEF; // changed from milk bucket to raw beef
    }

    public void equipKit(Player player) {
        super.equipKit(player);
        setArmor(Material.IRON_BOOTS, 0);
        setArmor(Material.IRON_LEGGINGS, 1);
        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);
        setArmor(Material.IRON_HELMET, 3);

        setItem(Material.IRON_AXE, 0, new InkShotgun());
        setItem(Material.IRON_SPADE, 1, new InkShotgun());

        addAttribute(new Regeneration(regeneration, 20));
        addAttribute(new GenericDoubleJump(0.9, 0.9, 1, Sound.GHAST_FIREBALL));
        addAttribute(new Stampede(3, 3));

        DisguiseManager.addDisguise(owner, new CowDisguise(owner));
    }
}
