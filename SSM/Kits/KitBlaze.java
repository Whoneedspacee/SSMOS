package SSM.Kits;

import SSM.Abilities.Firefly;
import SSM.Abilities.Inferno;
import SSM.Attributes.*;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.BlazeDisguise;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class KitBlaze extends Kit {

    public KitBlaze() {
        super();
        this.damage = 6;
        this.armor = 6;
        this.regeneration = 0.25;
        this.knockback = 1.50;
        this.name = "Blaze";
        this.menuItem = Material.BLAZE_POWDER;
        this.podium_mob_type = EntityType.BLAZE;
    }

    @Override
    public void initializeKit() {
        setArmorSlot(Material.CHAINMAIL_BOOTS, 0);
        setArmorSlot(Material.CHAINMAIL_LEGGINGS, 1);
        setArmorSlot(Material.CHAINMAIL_CHESTPLATE, 2);
        setArmorSlot(Material.CHAINMAIL_HELMET, 3);

        setAbility(new Inferno(), 0);
        setAbility(new Firefly(), 1);

        addAttribute(new Regeneration(regeneration));
        addAttribute(new Hunger());
        addAttribute(new Compass());
        addAttribute(new GenericDoubleJump(1.0, 1.0, 1, Sound.GHAST_FIREBALL));
        addAttribute(new ExpCharge(0.025F, 1, true, true, false));
        addAttribute(new Potion(PotionEffectType.SPEED, 0));
        addAttribute(new Potion(PotionEffectType.FIRE_RESISTANCE, 0));
        addAttribute(new FireImmunity());
        addAttribute(new FlamingKnockback(1.5));

        DisguiseManager.addDisguise(owner, new BlazeDisguise(owner));
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
        setItem(new ItemStack(Material.IRON_SWORD), 0);
        setItem(new ItemStack(Material.IRON_AXE), 1);
        setItem(Compass.COMPASS_ITEM, 2);
    }

}