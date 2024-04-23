package xyz.whoneedspacee.ssmos.kits.original;

import xyz.whoneedspacee.ssmos.managers.disguises.SquidDisguise;
import xyz.whoneedspacee.ssmos.abilities.original.FishFlurry;
import xyz.whoneedspacee.ssmos.abilities.original.InkShotgun;
import xyz.whoneedspacee.ssmos.abilities.original.SuperSquid;
import xyz.whoneedspacee.ssmos.attributes.Compass;
import xyz.whoneedspacee.ssmos.attributes.doublejumps.GenericDoubleJump;
import xyz.whoneedspacee.ssmos.attributes.Hunger;
import xyz.whoneedspacee.ssmos.attributes.Regeneration;
import xyz.whoneedspacee.ssmos.kits.Kit;
import xyz.whoneedspacee.ssmos.managers.DisguiseManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class KitSquid extends Kit {

    public KitSquid() {
        super();
        this.damage = 6;
        this.armor = 5;
        this.regeneration = 0.25;
        this.knockback = 1.5;
        this.name = "Sky Squid";
        this.menuItem = Material.INK_SACK;
        this.podium_mob_type = EntityType.SQUID;
    }

    @Override
    public void initializeKit() {
        setArmorSlot(Material.CHAINMAIL_BOOTS, 0);
        setArmorSlot(Material.CHAINMAIL_LEGGINGS, 1);
        setArmorSlot(Material.CHAINMAIL_CHESTPLATE, 2);

        setAbility(new InkShotgun(), 0);
        setAbility(new SuperSquid(), 1);
        setAbility(new FishFlurry(), 2);

        addAttribute(new Regeneration(regeneration));
        addAttribute(new Hunger());
        addAttribute(new Compass());
        addAttribute(new GenericDoubleJump(0.9, 0.9, Sound.GHAST_FIREBALL));

        DisguiseManager.addDisguise(owner, new SquidDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_AXE), 0);
        setItem(new ItemStack(Material.IRON_SWORD), 1);
        setItem(new ItemStack(Material.IRON_SPADE), 2);
        setItem(new ItemStack(Material.NETHER_STAR), 3);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_AXE), 0);
        setItem(new ItemStack(Material.IRON_SWORD), 1);
        setItem(new ItemStack(Material.IRON_SPADE), 2);
        setItem(Compass.COMPASS_ITEM, 3);
    }

}
