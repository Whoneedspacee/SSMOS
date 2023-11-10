package SSM.Kits;

import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Regeneration;
import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.ChickenDisguise;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class KitChicken extends Kit {

    public KitChicken() {
        super();
        this.damage = 4.5;
        this.armor = 2.5; // 2
        this.regeneration = 0.25;
        this.knockback = 2.0;
        this.name = "Chicken";
        this.menuItem = Material.EGG;
        this.podium_mob_type = EntityType.CHICKEN;
    }

    @Override
    public void initializeKit() {
        setArmorSlot(Material.CHAINMAIL_CHESTPLATE, 2);

        addAttribute(new Regeneration(regeneration, 20));
        addAttribute(new GenericDoubleJump(0.9, 0.9, 1, Sound.GHAST_FIREBALL));

        DisguiseManager.addDisguise(owner, new ChickenDisguise(owner));
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

}
