package xyz.whoneedspacee.ssmos.kits.original;

import xyz.whoneedspacee.ssmos.attributes.*;
import xyz.whoneedspacee.ssmos.managers.disguises.ZombieDisguise;
import xyz.whoneedspacee.ssmos.abilities.original.BileBlaster;
import xyz.whoneedspacee.ssmos.abilities.original.DeathsGrasp;
import xyz.whoneedspacee.ssmos.attributes.bowcharge.DamageBoost;
import xyz.whoneedspacee.ssmos.attributes.doublejumps.GenericDoubleJump;
import xyz.whoneedspacee.ssmos.kits.Kit;
import xyz.whoneedspacee.ssmos.managers.DisguiseManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class KitZombie extends Kit {

    public KitZombie() {
        super();
        this.damage = 6;
        this.armor = 5;
        this.regeneration = 0.25;
        this.knockback = 1.25;
        this.name = "Zombie";
        this.menuItem = Material.ROTTEN_FLESH;
        this.podium_mob_type = EntityType.ZOMBIE;
    }

    @Override
    public void initializeKit() {
        setArmorSlot(Material.CHAINMAIL_BOOTS, 0);
        setArmorSlot(Material.CHAINMAIL_LEGGINGS, 1);
        setArmorSlot(Material.CHAINMAIL_CHESTPLATE, 2);

        setAbility(new BileBlaster(), 0);
        setAbility(new DeathsGrasp(), 1);

        addAttribute(new Regeneration(regeneration));
        addAttribute(new Hunger());
        addAttribute(new Compass());
        addAttribute(new ItemGenerator(Material.ARROW, 1, 2, 2));
        addAttribute(new GenericDoubleJump(0.9, 0.9, Sound.GHAST_FIREBALL));
        addAttribute(new DamageBoost(1, 0.25, 6));
        addAttribute(new ArrowDamageModifier(-1));
        addAttribute(new MultiplyArrowKnockback(1.5));

        DisguiseManager.addDisguise(owner, new ZombieDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_AXE, 1), 0);
        setItem(new ItemStack(Material.BOW, 1), 1);
        setItem(new ItemStack(Material.ARROW), 2, getAttributeByClass(DamageBoost.class));
        setItem(new ItemStack(Material.NETHER_STAR), 3);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_AXE, 1), 0);
        setItem(new ItemStack(Material.BOW, 1), 1);
        setItem(Compass.COMPASS_ITEM, 2);
    }

}
