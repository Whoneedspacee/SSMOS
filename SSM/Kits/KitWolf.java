package SSM.Kits;

import SSM.Abilities.CubTackle;
import SSM.Abilities.WolfStrike;
import SSM.Attributes.Compass;
import SSM.Attributes.DoubleJumps.DirectDoubleJump;
import SSM.Attributes.Hunger;
import SSM.Attributes.Ravage;
import SSM.Attributes.Regeneration;
import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.WolfDisguise;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;

public class KitWolf extends Kit {

    public KitWolf() {
        super();
        this.damage = 5;
        this.armor = 4.5;
        this.regeneration = 0.25;
        this.knockback = 1.5;
        this.name = "Wolf";
        this.menuItem = Material.BONE;
    }

    @Override
    public void initializeKit() {
        setArmor(Material.CHAINMAIL_LEGGINGS, 1);
        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);

        setAbility(new CubTackle(), 0);
        setAbility(new WolfStrike(), 1);

        addAttribute(new Regeneration(regeneration));
        addAttribute(new Hunger());
        addAttribute(new Compass());
        addAttribute(new DirectDoubleJump(1.0, 1.0, 1, Sound.GHAST_FIREBALL));
        addAttribute(new Ravage());

        DisguiseManager.addDisguise(owner, new WolfDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_AXE), 0);
        setItem(new ItemStack(Material.IRON_SPADE), 1);
        setItem(new ItemStack(Material.BONE), 2, getAttributeByClass(Ravage.class));
        setItem(new ItemStack(Material.NETHER_STAR), 3);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_AXE, 1), 0);
        setItem(new ItemStack(Material.IRON_SPADE, 1), 1);
        setItem(Compass.COMPASS_ITEM, 2);
    }

}
