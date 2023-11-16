package ssm.kits;

import ssm.abilities.BoneExplosion;
import ssm.abilities.RopedArrow;
import ssm.attributes.*;
import ssm.attributes.bowcharge.Barrage;
import ssm.attributes.doublejumps.GenericDoubleJump;
import ssm.gamemanagers.DisguiseManager;
import ssm.gamemanagers.disguises.SkeletonDisguise;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class KitSkeleton extends Kit {

    public KitSkeleton() {
        super();
        this.damage = 5;
        this.armor = 6;
        this.regeneration = 0.2;
        this.knockback = 1.25;
        this.name = "Skeleton";
        this.menuItem = Material.BOW;
        this.podium_mob_type = EntityType.SKELETON;
    }

    @Override
    public void initializeKit() {
        setArmorSlot(Material.CHAINMAIL_BOOTS, 0);
        setArmorSlot(Material.CHAINMAIL_LEGGINGS, 1);
        setArmorSlot(Material.CHAINMAIL_CHESTPLATE, 2);
        setArmorSlot(Material.CHAINMAIL_HELMET, 3);

        setAbility(new BoneExplosion(), 0);
        setAbility(new RopedArrow(), 1);
        setAbility(new BoneExplosion(), 0);

        addAttribute(new Regeneration(regeneration));
        addAttribute(new Hunger());
        addAttribute(new Compass());
        addAttribute(new GenericDoubleJump(0.9, 0.9, 1, Sound.GHAST_FIREBALL));
        addAttribute(new ItemGenerator(Material.ARROW, 1, 3, 3));
        addAttribute(new Barrage(1, 0.3, 5));
        addAttribute(new FixedArrowDamage(6));
        addAttribute(new MultiplyArrowKnockback(1.5));

        DisguiseManager.addDisguise(owner, new SkeletonDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_AXE), 0);
        setItem(new ItemStack(Material.BOW), 1);
        setItem(new ItemStack(Material.ARROW), 2, getAttributeByClass(Barrage.class));
        setItem(new ItemStack(Material.NETHER_STAR), 3);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_AXE), 0);
        setItem(new ItemStack(Material.BOW), 1);
        setItem(Compass.COMPASS_ITEM, 2);
    }

}
