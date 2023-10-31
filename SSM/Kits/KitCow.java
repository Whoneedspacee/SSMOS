package SSM.Kits;

import SSM.Abilities.InkShotgun;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Regeneration;
import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.CowDisguise;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

public class KitCow extends Kit {

    public KitCow() {
        super();
        this.damage = 6;
        this.armor = 7;
        this.regeneration = 0.25;
        this.knockback = 1.1;
        this.name = "Cow";
        this.menuItem = Material.RAW_BEEF;
    }

    @Override
    public void initializeKit() {
        setArmor(Material.IRON_BOOTS, 0);
        setArmor(Material.IRON_LEGGINGS, 1);
        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);
        setArmor(Material.IRON_HELMET, 3);

        setAbility(new InkShotgun(), 0);
        setAbility(new InkShotgun(), 1);

        addAttribute(new Regeneration(regeneration, 20));
        addAttribute(new GenericDoubleJump(0.9, 0.9, 1, Sound.GHAST_FIREBALL));

        DisguiseManager.addDisguise(owner, new CowDisguise(owner));
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
