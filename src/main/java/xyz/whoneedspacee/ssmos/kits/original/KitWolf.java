package xyz.whoneedspacee.ssmos.kits.original;

import xyz.whoneedspacee.ssmos.managers.disguises.WolfDisguise;
import xyz.whoneedspacee.ssmos.abilities.original.CubTackle;
import xyz.whoneedspacee.ssmos.abilities.original.WolfStrike;
import xyz.whoneedspacee.ssmos.attributes.Compass;
import xyz.whoneedspacee.ssmos.attributes.doublejumps.DirectDoubleJump;
import xyz.whoneedspacee.ssmos.attributes.Hunger;
import xyz.whoneedspacee.ssmos.attributes.Ravage;
import xyz.whoneedspacee.ssmos.attributes.Regeneration;
import xyz.whoneedspacee.ssmos.kits.Kit;
import xyz.whoneedspacee.ssmos.managers.DisguiseManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class KitWolf extends Kit {

    public KitWolf() {
        super();
        this.damage = 5;
        this.armor = 4.5;
        this.regeneration = 0.25;
        this.knockback = 1.5;
        this.name = "Wolf";
        this.menuItem = Material.BONE;
        this.podium_mob_type = EntityType.WOLF;
    }

    @Override
    public void initializeKit() {
        setArmorSlot(Material.CHAINMAIL_LEGGINGS, 1);
        setArmorSlot(Material.CHAINMAIL_CHESTPLATE, 2);

        setAbility(new CubTackle(), 0);
        setAbility(new WolfStrike(), 1);

        addAttribute(new Regeneration(regeneration));
        addAttribute(new Hunger());
        addAttribute(new Compass());
        addAttribute(new DirectDoubleJump(1.0, 1.0, Sound.GHAST_FIREBALL));
        addAttribute(new Ravage());

        DisguiseManager.addDisguise(owner, new WolfDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_AXE), 0);
        setItem(new ItemStack(Material.IRON_SPADE), 1);
        setItem(new ItemStack(Material.BONE), 2, getAttributeByClass(Ravage.class));
        setItem(new ItemStack(Material.NETHER_STAR), 3);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_AXE, 1), 0);
        setItem(new ItemStack(Material.IRON_SPADE, 1), 1);
        setItem(Compass.COMPASS_ITEM, 2);
    }

}
