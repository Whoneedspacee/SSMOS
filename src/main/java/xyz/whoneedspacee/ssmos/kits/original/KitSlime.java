package xyz.whoneedspacee.ssmos.kits.original;

import xyz.whoneedspacee.ssmos.managers.disguises.SlimeDisguise;
import xyz.whoneedspacee.ssmos.abilities.original.SlimeRocket;
import xyz.whoneedspacee.ssmos.abilities.original.SlimeSlam;
import xyz.whoneedspacee.ssmos.attributes.Compass;
import xyz.whoneedspacee.ssmos.attributes.doublejumps.GenericDoubleJump;
import xyz.whoneedspacee.ssmos.attributes.ExpCharge;
import xyz.whoneedspacee.ssmos.attributes.Hunger;
import xyz.whoneedspacee.ssmos.attributes.Regeneration;
import xyz.whoneedspacee.ssmos.kits.Kit;
import xyz.whoneedspacee.ssmos.managers.DisguiseManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class KitSlime extends Kit {

    public KitSlime() {
        super();
        this.damage = 6;
        this.armor = 4;
        this.regeneration = 0.35;
        this.knockback = 1.75;
        this.name = "Slime";
        this.menuItem = Material.SLIME_BALL;
        this.podium_mob_type = EntityType.SLIME;
    }

    @Override
    public void initializeKit() {
        setArmorSlot(Material.CHAINMAIL_BOOTS, 0);
        setArmorSlot(Material.CHAINMAIL_CHESTPLATE, 2);
        setArmorSlot(Material.CHAINMAIL_HELMET, 3);

        setAbility(new SlimeRocket(), 0);
        setAbility(new SlimeSlam(), 1);

        addAttribute(new Regeneration(regeneration));
        addAttribute(new Hunger());
        addAttribute(new Compass());
        addAttribute(new GenericDoubleJump(1.2, 1.0, Sound.GHAST_FIREBALL));
        addAttribute(new ExpCharge(0.00321f, 1, true, true, false));

        DisguiseManager.addDisguise(owner, new SlimeDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_SWORD), 0);
        setItem(new ItemStack(Material.IRON_AXE), 1);
        setItem(new ItemStack(Material.NETHER_STAR), 2);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_SWORD), 0);
        setItem(new ItemStack(Material.IRON_AXE), 1);
        setItem(Compass.COMPASS_ITEM, 2);
    }

}
