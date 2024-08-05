package xyz.whoneedspacee.ssmos.kits.original;

import xyz.whoneedspacee.ssmos.managers.disguises.EndermanDisguise;
import xyz.whoneedspacee.ssmos.abilities.original.Blink;
import xyz.whoneedspacee.ssmos.abilities.original.BlockToss;
import xyz.whoneedspacee.ssmos.attributes.Compass;
import xyz.whoneedspacee.ssmos.attributes.doublejumps.GenericDoubleJump;
import xyz.whoneedspacee.ssmos.attributes.Hunger;
import xyz.whoneedspacee.ssmos.attributes.Regeneration;
import xyz.whoneedspacee.ssmos.attributes.Teleport;
import xyz.whoneedspacee.ssmos.kits.Kit;
import xyz.whoneedspacee.ssmos.managers.DisguiseManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
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
        this.podium_mob_type = EntityType.ENDERMAN;
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
        addAttribute(new GenericDoubleJump(0.9, 0.9, Sound.GHAST_FIREBALL));
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
