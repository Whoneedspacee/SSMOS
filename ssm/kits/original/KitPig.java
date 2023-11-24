package ssm.kits.original;

import ssm.abilities.original.BabyBaconBombs;
import ssm.abilities.original.BouncyBacon;
import ssm.attributes.*;
import ssm.attributes.doublejumps.GenericDoubleJump;
import ssm.kits.Kit;
import ssm.managers.DisguiseManager;
import ssm.managers.disguises.PigDisguise;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class KitPig extends Kit {

    public KitPig() {
        super();
        this.damage = 5;
        this.armor = 5;
        this.regeneration = 0.25;
        this.knockback = 1.5;
        this.name = "Pig";
        this.menuItem = Material.PORK;
        this.podium_mob_type = EntityType.PIG;
    }

    @Override
    public void initializeKit() {
        setArmorSlot(Material.CHAINMAIL_BOOTS, 0);
        setArmorSlot(Material.CHAINMAIL_LEGGINGS, 1);
        setArmorSlot(Material.CHAINMAIL_CHESTPLATE, 2);

        setAbility(new BouncyBacon(), 0);
        setAbility(new BabyBaconBombs(), 1);

        addAttribute(new Regeneration(regeneration));
        addAttribute(new Hunger());
        addAttribute(new Compass());
        addAttribute(new GenericDoubleJump(0.9, 0.9, 1, Sound.GHAST_FIREBALL));
        addAttribute(new ExpCharge(0.005F, 1, true, true, false));
        addAttribute(new NetherPig());

        DisguiseManager.addDisguise(owner, new PigDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_AXE), 0);
        setItem(new ItemStack(Material.IRON_SPADE), 1);
        setItem(new ItemStack(Material.PORK), 2, getAttributeByClass(NetherPig.class));
        setItem(new ItemStack(Material.NETHER_STAR), 3);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_AXE), 0);
        setItem(new ItemStack(Material.IRON_SPADE), 1);
        setItem(Compass.COMPASS_ITEM, 2);
    }

}
