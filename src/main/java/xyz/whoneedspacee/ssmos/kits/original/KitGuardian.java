package xyz.whoneedspacee.ssmos.kits.original;

import xyz.whoneedspacee.ssmos.managers.disguises.GuardianDisguise;
import xyz.whoneedspacee.ssmos.abilities.original.TargetLaser;
import xyz.whoneedspacee.ssmos.abilities.original.WaterSplash;
import xyz.whoneedspacee.ssmos.abilities.original.WhirlpoolAxe;
import xyz.whoneedspacee.ssmos.attributes.Compass;
import xyz.whoneedspacee.ssmos.attributes.doublejumps.GenericDoubleJump;
import xyz.whoneedspacee.ssmos.attributes.Hunger;
import xyz.whoneedspacee.ssmos.attributes.Regeneration;
import xyz.whoneedspacee.ssmos.attributes.Thorns;
import xyz.whoneedspacee.ssmos.kits.Kit;
import xyz.whoneedspacee.ssmos.managers.DisguiseManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class KitGuardian extends Kit {

    public KitGuardian() {
        super();
        this.damage = 5;
        this.armor = 4.5;
        this.regeneration = 0.25;
        this.knockback = 1.25;
        this.name = "Guardian";
        this.menuItem = Material.PRISMARINE_SHARD;
        this.podium_mob_type = EntityType.GUARDIAN;
    }

    @Override
    public void initializeKit() {
        setArmorSlot(Material.DIAMOND_BOOTS, 0);
        setArmorSlot(Material.DIAMOND_LEGGINGS, 1);

        setAbility(new WhirlpoolAxe(), 0);
        setAbility(new WaterSplash(), 1);
        setAbility(new TargetLaser(), 2);

        addAttribute(new Regeneration(regeneration));
        addAttribute(new Hunger());
        addAttribute(new Compass());
        addAttribute(new GenericDoubleJump(0.9, 0.9, Sound.GHAST_FIREBALL));
        addAttribute(new Thorns(10, 0.66, 0.66));

        DisguiseManager.addDisguise(owner, new GuardianDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_AXE), 0);
        setItem(new ItemStack(Material.IRON_SWORD), 1);
        setItem(new ItemStack(Material.IRON_PICKAXE), 2);
        setItem(new ItemStack(Material.PRISMARINE_SHARD), 3, getAttributeByClass(Thorns.class));
        setItem(new ItemStack(Material.NETHER_STAR), 4);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_AXE), 0);
        setItem(new ItemStack(Material.IRON_SWORD), 1);
        setItem(new ItemStack(Material.IRON_PICKAXE), 2);
        setItem(Compass.COMPASS_ITEM, 3);
    }

}
