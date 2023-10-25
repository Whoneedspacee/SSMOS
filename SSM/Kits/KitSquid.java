package SSM.Kits;

import SSM.Abilities.FishFlurry;
import SSM.Abilities.InkShotgun;
import SSM.Abilities.SuperSquid;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Regeneration;
import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.SquidDisguise;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitSquid extends Kit {

    public KitSquid() {
        super();
        this.damage = 6;
        this.armor = 5;
        this.regeneration = 0.25;
        this.knockback = 1.5;
        this.name = "Sky Squid";
        this.menuItem = Material.INK_SACK;
    }

    @Override
    public void initializeKit() {
        setArmor(Material.CHAINMAIL_BOOTS, 0);
        setArmor(Material.CHAINMAIL_LEGGINGS, 1);
        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);

        setAbility(new InkShotgun(), 0);
        setAbility(new SuperSquid(), 1);
        setAbility(new FishFlurry(), 2);

        addAttribute(new Regeneration(regeneration, 20));
        addAttribute(new GenericDoubleJump(0.9, 0.9, 1, Sound.GHAST_FIREBALL));

        DisguiseManager.addDisguise(owner, new SquidDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_AXE, 1), 0);
        setItem(new ItemStack(Material.IRON_SWORD, 1), 1);
        setItem(new ItemStack(Material.IRON_SPADE, 1), 2);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_AXE, 1), 0);
        setItem(new ItemStack(Material.IRON_SWORD, 1), 1);
        setItem(new ItemStack(Material.IRON_SPADE, 1), 2);
    }

}
