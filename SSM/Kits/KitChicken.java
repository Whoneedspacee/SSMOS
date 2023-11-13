package SSM.Kits;

import SSM.Abilities.ChickenMissile;
import SSM.Abilities.EggBlaster;
import SSM.Attributes.*;
import SSM.Attributes.DoubleJumps.Custom.ChickenJump;
import SSM.Attributes.DoubleJumps.EnergyDoubleJump;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.ChickenDisguise;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class KitChicken extends Kit {

    public KitChicken() {
        super();
        this.damage = 4.5;
        this.armor = 2.5;
        this.regeneration = 0.25;
        this.knockback = 2.0;
        this.name = "Chicken";
        this.menuItem = Material.EGG;
        this.podium_mob_type = EntityType.CHICKEN;
    }

    @Override
    public void initializeKit() {
        setArmorSlot(Material.CHAINMAIL_CHESTPLATE, 2);

        setAbility(new EggBlaster(), 0);
        setAbility(new ChickenMissile(), 1);

        addAttribute(new Regeneration(regeneration));
        addAttribute(new Hunger());
        addAttribute(new Compass());
        addAttribute(new ChickenJump(0.725, 10, Integer.MAX_VALUE, Sound.BAT_TAKEOFF, 0.167f));
        addAttribute(new ExpCharge(0.03F, 1, false, true, true));

        DisguiseManager.addDisguise(owner, new ChickenDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_SWORD), 0);
        setItem(new ItemStack(Material.IRON_AXE), 1);
        setItem(new ItemStack(Material.FEATHER), 2, getAttributeByClass(ChickenJump.class));
        setItem(new ItemStack(Material.NETHER_STAR), 3);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_SWORD), 0);
        setItem(new ItemStack(Material.IRON_AXE), 1);
        setItem(Compass.COMPASS_ITEM, 2);
    }

}
