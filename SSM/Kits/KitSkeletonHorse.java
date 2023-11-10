package SSM.Kits;

import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Regeneration;
import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.SkeletonHorseDisguise;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.ItemStack;

public class KitSkeletonHorse extends Kit {

    public KitSkeletonHorse() {
        super();
        this.damage = 6;
        this.armor = 6.5;
        this.regeneration = 0.3;
        this.knockback = 1.25;
        this.name = "Skeleton Horse";
        this.menuItem = Material.BONE;
        this.podium_mob_type = EntityType.HORSE;
    }

    @Override
    public void initializeKit() {
        setArmorSlot(Material.IRON_BOOTS, 0);
        setArmorSlot(Material.CHAINMAIL_LEGGINGS, 1);
        setArmorSlot(Material.CHAINMAIL_CHESTPLATE, 2);
        setArmorSlot(Material.CHAINMAIL_HELMET, 3);

        addAttribute(new Regeneration(regeneration, 20));
        addAttribute(new GenericDoubleJump(1.0, 1.0, 1, Sound.GHAST_FIREBALL));

        DisguiseManager.addDisguise(owner, new SkeletonHorseDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_AXE, 1), 0);
        setItem(new ItemStack(Material.IRON_SPADE, 1), 1);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_AXE, 1), 0);
        setItem(new ItemStack(Material.IRON_SPADE, 1), 1);
    }

    @Override
    public Entity getNewPodiumMob(Location spawn_location) {
        Entity entity = super.getNewPodiumMob(spawn_location);
        if(entity instanceof Horse) {
            Horse horse = (Horse) entity;
            horse.setVariant(Horse.Variant.SKELETON_HORSE);
        }
        return entity;
    }

}
