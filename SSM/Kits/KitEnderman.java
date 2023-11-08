package SSM.Kits;

import SSM.Abilities.Blink;
import SSM.Abilities.BlockToss;
import SSM.Attributes.Compass;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Hunger;
import SSM.Attributes.Regeneration;
import SSM.Attributes.Teleport;
import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.EndermanDisguise;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

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

    @Override
    public void initializeKit() {
        setArmorSlot(Material.CHAINMAIL_BOOTS, 0);
        setArmorSlot(Material.CHAINMAIL_LEGGINGS, 1);
        setArmorSlot(Material.CHAINMAIL_CHESTPLATE, 2);
        setArmorSlot(Material.CHAINMAIL_HELMET, 3);

        setAbility(new BlockToss(), 0);
        setAbility(new Blink(), 1);

        addAttribute(new Regeneration(regeneration));
        addAttribute(new Hunger());
        addAttribute(new Compass());
        addAttribute(new GenericDoubleJump(0.9, 0.9, 1, Sound.GHAST_FIREBALL));
        addAttribute(new Teleport());

        DisguiseManager.addDisguise(owner, new EndermanDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_SWORD), 0);
        setItem(new ItemStack(Material.IRON_AXE), 1);
        setItem(new ItemStack(Material.EYE_OF_ENDER), 2, getAttributeByClass(Teleport.class));
        setItem(new ItemStack(Material.NETHER_STAR), 3);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_SWORD, 1), 0);
        setItem(new ItemStack(Material.IRON_AXE, 1), 1);
        setItem(Compass.COMPASS_ITEM, 2);
    }

}
