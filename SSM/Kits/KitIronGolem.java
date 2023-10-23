package SSM.Kits;

import SSM.Abilities.Fissure;
import SSM.Abilities.IronHook;
import SSM.Abilities.SeismicSlam;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Potion;
import SSM.Attributes.Regeneration;
import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.IronGolemDisguise;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class KitIronGolem extends Kit {

    public KitIronGolem() {
        super();
        this.damage = 7;
        this.armor = 8;
        this.regeneration = 0.2;
        this.knockback = 1.0;
        this.name = "Iron Golem";
        this.menuItem = Material.IRON_BLOCK;
    }

    @Override
    public void initializeKit() {
        setArmor(Material.DIAMOND_BOOTS, 0);
        setArmor(Material.IRON_LEGGINGS, 1);
        setArmor(Material.IRON_CHESTPLATE, 2);
        setArmor(Material.IRON_HELMET, 3);

        setAbility(new Fissure(), 0);
        setAbility(new IronHook(), 1);
        setAbility(new SeismicSlam(), 2);

        addAttribute(new Regeneration(regeneration, 20));
        addAttribute(new GenericDoubleJump(0.9, 0.9, 1, Sound.GHAST_FIREBALL));
        addAttribute(new Potion(PotionEffectType.SLOW, 1));

        DisguiseManager.addDisguise(owner, new IronGolemDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_AXE, 1), 0);
        setItem(new ItemStack(Material.IRON_PICKAXE, 1), 1);
        setItem(new ItemStack(Material.IRON_SPADE, 1), 2);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_AXE, 1), 0);
        setItem(new ItemStack(Material.IRON_PICKAXE, 1), 1);
        setItem(new ItemStack(Material.IRON_SPADE, 1), 2);
    }

}
