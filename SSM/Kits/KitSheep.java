package SSM.Kits;

import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Regeneration;
import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.SheepDisguise;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;

public class KitSheep extends Kit {

    public KitSheep() {
        super();
        this.damage = 5;
        this.armor = 5;
        this.regeneration = 0.25;
        this.knockback = 1.7;
        this.name = "Sheep";
        this.menuItem = Material.WOOL;
        this.podium_mob_type = EntityType.SHEEP;
    }

    @Override
    public void initializeKit() {
        setArmorSlot(Material.CHAINMAIL_BOOTS, 0);
        setArmorSlot(Material.CHAINMAIL_LEGGINGS, 1);
        setArmorSlot(Material.CHAINMAIL_CHESTPLATE, 2);

        addAttribute(new Regeneration(regeneration, 20));
        addAttribute(new GenericDoubleJump(1, 1, 1, Sound.GHAST_FIREBALL));

        DisguiseManager.addDisguise(owner, new SheepDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_SWORD, 1), 0);
        setItem(new ItemStack(Material.IRON_AXE, 1), 1);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_SWORD, 1), 0);
        setItem(new ItemStack(Material.IRON_AXE, 1), 1);
    }

    @Override
    public Entity getNewPodiumMob(Location spawn_location) {
        Entity entity = super.getNewPodiumMob(spawn_location);
        if(entity instanceof Sheep) {
            Sheep sheep = (Sheep) entity;
            sheep.setColor(DyeColor.WHITE);
        }
        return entity;
    }

}
