package SSM.Kits;

import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Potion;
import SSM.Attributes.Regeneration;
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

        addAttribute(new Regeneration(regeneration, 20));
        addAttribute(new GenericDoubleJump(1.0, 1.0, 1, Sound.GHAST_FIREBALL));
        addAttribute(new Potion(PotionEffectType.SPEED, 0));

        DisguiseManager.addDisguise(owner, new BlazeDisguise(owner));
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