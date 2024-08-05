package xyz.whoneedspacee.ssmos.kits.original;

import xyz.whoneedspacee.ssmos.attributes.Compass;
import xyz.whoneedspacee.ssmos.attributes.Hunger;
import xyz.whoneedspacee.ssmos.attributes.Potion;
import xyz.whoneedspacee.ssmos.attributes.Regeneration;
import xyz.whoneedspacee.ssmos.managers.disguises.IronGolemDisguise;
import xyz.whoneedspacee.ssmos.abilities.original.Fissure;
import xyz.whoneedspacee.ssmos.abilities.original.IronHook;
import xyz.whoneedspacee.ssmos.abilities.original.SeismicSlam;
import xyz.whoneedspacee.ssmos.attributes.doublejumps.GenericDoubleJump;
import xyz.whoneedspacee.ssmos.kits.Kit;
import xyz.whoneedspacee.ssmos.managers.DisguiseManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class KitIronGolem extends Kit {

    public KitIronGolem() {
        super();
        this.damage = 7;
        this.armor = 8;
        this.regeneration = 0.2;
        this.knockback = 1.0;
        this.name = "Iron Golem";
        this.menuItem = Material.IRON_BLOCK;
        this.podium_mob_type = EntityType.IRON_GOLEM;
    }

    @Override
    public void initializeKit() {
        setArmorSlot(Material.DIAMOND_BOOTS, 0);
        setArmorSlot(Material.IRON_LEGGINGS, 1);
        setArmorSlot(Material.IRON_CHESTPLATE, 2);
        setArmorSlot(Material.IRON_HELMET, 3);

        setAbility(new Fissure(), 0);
        setAbility(new IronHook(), 1);
        setAbility(new SeismicSlam(), 2);

        addAttribute(new Regeneration(regeneration));
        addAttribute(new Hunger());
        addAttribute(new Compass());
        addAttribute(new GenericDoubleJump(0.9, 0.9, Sound.GHAST_FIREBALL));
        addAttribute(new Potion(PotionEffectType.SLOW, 0));

        DisguiseManager.addDisguise(owner, new IronGolemDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_AXE), 0);
        setItem(new ItemStack(Material.IRON_PICKAXE), 1);
        setItem(new ItemStack(Material.IRON_SPADE), 2);
        setItem(new ItemStack(Material.NETHER_STAR), 3);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_AXE), 0);
        setItem(new ItemStack(Material.IRON_PICKAXE), 1);
        setItem(new ItemStack(Material.IRON_SPADE), 2);
        setItem(Compass.COMPASS_ITEM, 3);
    }

}
