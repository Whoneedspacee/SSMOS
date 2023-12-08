package ssm.kits.ssmos;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import ssm.abilities.original.Firefly;
import ssm.abilities.original.Inferno;
import ssm.abilities.ssmos.FlameFissure;
import ssm.abilities.ssmos.MagmaBoulder;
import ssm.attributes.*;
import ssm.attributes.doublejumps.GenericDoubleJump;
import ssm.kits.Kit;
import ssm.kits.original.KitBlaze;
import ssm.managers.DisguiseManager;
import ssm.managers.disguises.BlazeDisguise;

public class OSKitBlaze extends KitBlaze {

    public OSKitBlaze() {
        super();
    }

    @Override
    public void initializeKit() {
        super.initializeKit();
        setAbility(new MagmaBoulder(), 1);
        setAbility(new FlameFissure(), 2);
        ExpCharge charge = getAttributeByClass(ExpCharge.class);
        if(charge != null) {
            charge.expAdd = 0.01f;
        }
        removeAttribute(getAttributeByClass(Potion.class));
        removeAttribute(getAttributeByClass(Potion.class));
        removeAttribute(getAttributeByClass(FireImmunity.class));
        addAttribute(new Potion(PotionEffectType.SPEED, 0));
        FlamingKnockback flamingKnockback = getAttributeByClass(FlamingKnockback.class);
        if(flamingKnockback != null) {
            flamingKnockback.knockback_multiplier = 2;
            flamingKnockback.extinguish_after = true;
            flamingKnockback.melee_only = true;
        }
        Inferno inferno = getAttributeByClass(Inferno.class);
        if(inferno != null) {
            inferno.energy_per_fire = 0.1f;
            inferno.fire_ticks_added = 20;
        }
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_SWORD), 0);
        setItem(new ItemStack(Material.IRON_AXE), 1);
        setItem(new ItemStack(Material.IRON_PICKAXE), 2);
        setItem(new ItemStack(Material.BLAZE_ROD), 3, getAttributeByClass(FlamingKnockback.class));
        setItem(new ItemStack(Material.NETHER_STAR), 4);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_SWORD), 0);
        setItem(new ItemStack(Material.IRON_AXE), 1);
        setItem(new ItemStack(Material.IRON_PICKAXE), 2);
        setItem(Compass.COMPASS_ITEM, 3);
    }

}