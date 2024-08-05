package xyz.whoneedspacee.ssmos.kits.original;

import xyz.whoneedspacee.ssmos.managers.disguises.CowDisguise;
import xyz.whoneedspacee.ssmos.abilities.original.AngryHerd;
import xyz.whoneedspacee.ssmos.abilities.original.MilkSpiral;
import xyz.whoneedspacee.ssmos.attributes.Compass;
import xyz.whoneedspacee.ssmos.attributes.doublejumps.GenericDoubleJump;
import xyz.whoneedspacee.ssmos.attributes.Hunger;
import xyz.whoneedspacee.ssmos.attributes.Regeneration;
import xyz.whoneedspacee.ssmos.attributes.Stampede;
import xyz.whoneedspacee.ssmos.kits.Kit;
import xyz.whoneedspacee.ssmos.managers.DisguiseManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class KitCow extends Kit {

    public KitCow() {
        super();
        this.damage = 6;
        this.armor = 7;
        this.regeneration = 0.25;
        this.knockback = 1.1;
        this.name = "Cow";
        this.menuItem = Material.RAW_BEEF;
        this.podium_mob_type = EntityType.COW;
    }

    @Override
    public void initializeKit() {
        setArmorSlot(Material.IRON_BOOTS, 0);
        setArmorSlot(Material.IRON_LEGGINGS, 1);
        setArmorSlot(Material.CHAINMAIL_CHESTPLATE, 2);
        setArmorSlot(Material.IRON_HELMET, 3);

        setAbility(new AngryHerd(), 0);
        setAbility(new MilkSpiral(), 1);

        addAttribute(new Regeneration(regeneration));
        addAttribute(new Hunger());
        addAttribute(new Compass());
        addAttribute(new GenericDoubleJump(0.9, 0.9, Sound.GHAST_FIREBALL));
        addAttribute(new Stampede());

        DisguiseManager.addDisguise(owner, new CowDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_AXE), 0);
        setItem(new ItemStack(Material.IRON_SPADE), 1);
        setItem(new ItemStack(Material.LEATHER), 2, getAttributeByClass(Stampede.class));
        setItem(new ItemStack(Material.NETHER_STAR), 3);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_AXE), 0);
        setItem(new ItemStack(Material.IRON_SPADE), 1);
        setItem(Compass.COMPASS_ITEM, 2);
    }

}
