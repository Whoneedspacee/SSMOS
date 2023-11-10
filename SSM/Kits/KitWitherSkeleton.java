package SSM.Kits;

import SSM.Abilities.GuidedWitherSkull;
import SSM.Abilities.WitherImage;
import SSM.Attributes.Compass;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Hunger;
import SSM.Attributes.Regeneration;
import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.WitherSkeletonDisguise;
import SSM.GameManagers.GameManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;

public class KitWitherSkeleton extends Kit {

    public KitWitherSkeleton() {
        super();
        this.damage = 6;
        this.armor = 6;
        this.regeneration = 0.3;
        this.knockback = 1.2;
        this.name = "Wither Skeleton";
        this.menuItem = Material.IRON_SWORD;
        this.podium_mob_type = EntityType.SKELETON;
    }

    @Override
    public void initializeKit() {
        setArmorSlot(Material.CHAINMAIL_BOOTS, 0);
        setArmorSlot(Material.CHAINMAIL_LEGGINGS, 1);
        setArmorSlot(Material.CHAINMAIL_CHESTPLATE, 2);
        setArmorSlot(Material.CHAINMAIL_HELMET, 3);

        setAbility(new GuidedWitherSkull(), 0);
        setAbility(new WitherImage(), 1);

        addAttribute(new Regeneration(regeneration));
        addAttribute(new Hunger());
        addAttribute(new Compass());
        addAttribute(new GenericDoubleJump(0.9, 0.9, 1, Sound.GHAST_FIREBALL));

        DisguiseManager.addDisguise(owner, new WitherSkeletonDisguise(owner));
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

    @Override
    public Entity getNewPodiumMob(Location spawn_location) {
        Entity entity = super.getNewPodiumMob(spawn_location);
        if(entity instanceof Skeleton) {
            Skeleton skeleton = (Skeleton) entity;
            skeleton.setSkeletonType(Skeleton.SkeletonType.WITHER);
        }
        return entity;
    }

}
