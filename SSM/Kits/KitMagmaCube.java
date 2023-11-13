package SSM.Kits;

import SSM.Abilities.FlameDash;
import SSM.Abilities.MagmaBlast;
import SSM.Attributes.*;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.MagmaCubeDisguise;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class KitMagmaCube extends Kit {

    public KitMagmaCube() {
        super();
        this.damage = 5;
        this.armor = 4;
        this.regeneration = 0.35;
        this.knockback = 1.75;
        this.name = "Magma Cube";
        this.menuItem = Material.MAGMA_CREAM;
        this.podium_mob_type = EntityType.MAGMA_CUBE;
    }

    @Override
    public void initializeKit() {
        setArmorSlot(Material.CHAINMAIL_BOOTS, 0);
        setArmorSlot(Material.CHAINMAIL_CHESTPLATE, 2);
        setArmorSlot(Material.CHAINMAIL_HELMET, 3);

        setAbility(new MagmaBlast(), 0);
        setAbility(new FlameDash(), 1);

        addAttribute(new Regeneration(regeneration));
        addAttribute(new Hunger());
        addAttribute(new Compass());
        addAttribute(new GenericDoubleJump(1.2, 1.0, 1, Sound.GHAST_FIREBALL));
        addAttribute(new FireImmunity());
        addAttribute(new FuelTheFire());

        DisguiseManager.addDisguise(owner, new MagmaCubeDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_AXE), 0);
        setItem(new ItemStack(Material.IRON_SPADE), 1);
        setItem(new ItemStack(Material.BLAZE_POWDER), 2, getAttributeByClass(FuelTheFire.class));
        setItem(new ItemStack(Material.NETHER_STAR), 3);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_AXE), 0);
        setItem(new ItemStack(Material.IRON_SPADE), 1);
        setItem(Compass.COMPASS_ITEM, 2);
    }

}
