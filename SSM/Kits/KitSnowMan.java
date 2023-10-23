package SSM.Kits;

import SSM.Abilities.Blizzard;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.ExpCharge;
import SSM.Attributes.Regeneration;
import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.SnowmanDisguise;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitSnowMan extends Kit {

    public KitSnowMan() {
        super();
        this.damage = 6;
        this.armor = 6;
        this.regeneration = 0.3;
        this.knockback = 1.4;
        this.name = "Snowman";
        this.menuItem = Material.SNOW_BALL;
    }

    @Override
    public void initializeKit() {
        setArmor(Material.CHAINMAIL_BOOTS, 0);
        setArmor(Material.CHAINMAIL_LEGGINGS, 1);
        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);
        setArmor(Material.CHAINMAIL_HELMET, 3);

        setAbility(new Blizzard(), 0);

        addAttribute(new Regeneration(regeneration, 20));
        addAttribute(new GenericDoubleJump(0.9, 0.9, 1, Sound.GHAST_FIREBALL));
        addAttribute(new ExpCharge(0.01F, 1, false));

        DisguiseManager.addDisguise(owner, new SnowmanDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_SWORD, 1), 0);
        setItem(new ItemStack(Material.IRON_AXE, 1), 1);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_SWORD, 1), 0);
        setItem(new ItemStack(Material.IRON_AXE, 1), 1);
    }

}
