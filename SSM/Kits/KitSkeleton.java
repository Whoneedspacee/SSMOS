package SSM.Kits;

import SSM.Abilities.BoneExplosion;
import SSM.Abilities.RopedArrow;
import SSM.Attributes.BowCharge.Barrage;
import SSM.Attributes.Compass;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Hunger;
import SSM.Attributes.ItemGenerator;
import SSM.Attributes.Regeneration;
import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.SkeletonDisguise;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
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
    }

    @Override
    public void initializeKit() {
        setArmor(Material.CHAINMAIL_BOOTS, 0);
        setArmor(Material.CHAINMAIL_LEGGINGS, 1);
        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);
        setArmor(Material.CHAINMAIL_HELMET, 3);

        setAbility(new BoneExplosion(), 0);
        setAbility(new RopedArrow(), 1);
        setAbility(new BoneExplosion(), 0);

        addAttribute(new Regeneration(regeneration));
        addAttribute(new Hunger());
        addAttribute(new GenericDoubleJump(0.9, 0.9, 1, Sound.GHAST_FIREBALL));
        addAttribute(new Compass());
        addAttribute(new ItemGenerator(Material.ARROW, 1, 3, 3));
        addAttribute(new Barrage(1.1, 0.25, 5));

        DisguiseManager.addDisguise(owner, new SkeletonDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_AXE, 1), 0);
        setItem(new ItemStack(Material.BOW, 1), 1);
        setItem(new ItemStack(Material.ARROW, 1), 2, getAttributeByName("Barrage"));
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_AXE, 1), 0);
        setItem(new ItemStack(Material.BOW, 1), 1);
        setItem(new ItemStack(Material.COMPASS, 1), 2);
    }

}
