package ssm.kits.ssmos;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import ssm.abilities.original.Inferno;
import ssm.abilities.ssmos.HeatStrike;
import ssm.abilities.ssmos.IgnitedAirstream;
import ssm.abilities.ssmos.SoulDetach;
import ssm.attributes.*;
import ssm.attributes.doublejumps.GenericDoubleJump;
import ssm.kits.Kit;
import ssm.managers.DisguiseManager;
import ssm.managers.disguises.BlazeDisguise;
import ssm.managers.disguises.PigmanDisguise;

public class KitPigman extends Kit {

    public KitPigman() {
        super();
        this.damage = 6;
        this.armor = 4.5;
        this.regeneration = 0.25;
        this.knockback = 1.50;
        this.name = "Pigman";
        this.menuItem = Material.GOLD_INGOT;
        this.podium_mob_type = EntityType.PIG_ZOMBIE;
    }

    @Override
    public void initializeKit() {
        setArmorSlot(Material.GOLD_LEGGINGS, 1);
        setArmorSlot(Material.GOLD_CHESTPLATE, 2);
        setArmorSlot(Material.GOLD_BOOTS, 0);

        setAbility(new IgnitedAirstream(), 0);
        setAbility(new SoulDetach(), 1);
        addAttribute(new HeatStrike());

        addAttribute(new Regeneration(regeneration));
        addAttribute(new Hunger());
        addAttribute(new Compass());
        addAttribute(new GenericDoubleJump(1.6, 0.6, Sound.GHAST_FIREBALL));

        DisguiseManager.addDisguise(owner, new PigmanDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_SWORD), 0);
        setItem(new ItemStack(Material.IRON_AXE), 1);
        setItem(new ItemStack(Material.BLAZE_ROD), 2, getAttributeByClass(FlamingKnockback.class));
        setItem(new ItemStack(Material.NETHER_STAR), 3);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.GOLD_AXE), 0);
        setItem(new ItemStack(Material.GOLD_SPADE), 1);
        setItem(Compass.COMPASS_ITEM, 2);
    }

}