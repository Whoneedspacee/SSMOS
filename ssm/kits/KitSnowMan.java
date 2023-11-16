package ssm.kits;

import ssm.abilities.Blizzard;
import ssm.abilities.IcePath;
import ssm.attributes.*;
import ssm.attributes.doublejumps.GenericDoubleJump;
import ssm.gamemanagers.DisguiseManager;
import ssm.gamemanagers.disguises.SnowmanDisguise;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class KitSnowMan extends Kit {

    public KitSnowMan() {
        super();
        this.damage = 6;
        this.armor = 6;
        this.regeneration = 0.3;
        this.knockback = 1.4;
        this.name = "Snowman";
        this.menuItem = Material.SNOW_BALL;
        this.podium_mob_type = EntityType.SNOWMAN;
    }

    @Override
    public void initializeKit() {
        setArmorSlot(Material.CHAINMAIL_BOOTS, 0);
        setArmorSlot(Material.CHAINMAIL_LEGGINGS, 1);
        setArmorSlot(Material.CHAINMAIL_CHESTPLATE, 2);
        setArmorSlot(Material.CHAINMAIL_HELMET, 3);

        setAbility(new Blizzard(), 0);
        setAbility(new IcePath(), 1);

        addAttribute(new Regeneration(regeneration));
        addAttribute(new Hunger());
        addAttribute(new Compass());
        addAttribute(new GenericDoubleJump(0.9, 0.9, 1, Sound.GHAST_FIREBALL));
        addAttribute(new ExpCharge(0.01666666667F, 1, true, true, false));
        addAttribute(new ArcticAura());
        addAttribute(new SnowDamage());

        DisguiseManager.addDisguise(owner, new SnowmanDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_SWORD), 0);
        setItem(new ItemStack(Material.IRON_AXE), 1);
        setItem(new ItemStack(Material.SNOW_BLOCK), 2, getAttributeByClass(ArcticAura.class));
        setItem(new ItemStack(Material.NETHER_STAR), 3);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_SWORD), 0);
        setItem(new ItemStack(Material.IRON_AXE), 1);
        setItem(Compass.COMPASS_ITEM, 2);
    }

}
