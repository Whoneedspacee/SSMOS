package SSM.Kits;

import SSM.Attributes.DoubleJumps.DirectDoubleJump;
import SSM.Attributes.Regeneration;
import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.WolfDisguise;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

public class KitWolf extends Kit {

    public KitWolf() {
        super();
        this.damage = 5;
        this.armor = 4.5;
        this.regeneration = 0.25;
        this.knockback = 1.6;
        this.name = "Wolf";
        this.menuItem = Material.BONE;
    }

    @Override
    public void initializeKit() {
        setArmor(Material.CHAINMAIL_LEGGINGS, 1);
        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);

        addAttribute(new Regeneration(regeneration, 20));
        addAttribute(new DirectDoubleJump(1.0, 1.0, 1, Sound.GHAST_FIREBALL));

        DisguiseManager.addDisguise(owner, new WolfDisguise(owner));
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

}
