package ssm.kits.original;

import ssm.abilities.original.BatWave;
import ssm.abilities.original.DazePotion;
import ssm.attributes.BatLeash;
import ssm.attributes.Compass;
import ssm.attributes.doublejumps.GenericDoubleJump;
import ssm.attributes.Hunger;
import ssm.attributes.Regeneration;
import ssm.kits.Kit;
import ssm.managers.DisguiseManager;
import ssm.managers.disguises.WitchDisguise;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class KitWitch extends Kit {

    public KitWitch() {
        super();
        this.damage = 6;
        this.armor = 5;
        this.regeneration = 0.3;
        this.knockback = 1.5;
        this.name = "Witch";
        this.menuItem = Material.POTION;
        this.podium_mob_type = EntityType.WITCH;
    }

    @Override
    public void initializeKit() {
        setArmorSlot(Material.CHAINMAIL_BOOTS, 0);
        setArmorSlot(Material.CHAINMAIL_LEGGINGS, 1);
        setArmorSlot(Material.CHAINMAIL_CHESTPLATE, 2);

        setAbility(new DazePotion(), 0);
        setAbility(new BatWave(), 1);

        addAttribute(new Regeneration(regeneration));
        addAttribute(new Hunger());
        addAttribute(new Compass());
        addAttribute(new GenericDoubleJump(0.9, 0.9, Sound.GHAST_FIREBALL));
        addAttribute(new BatLeash());

        DisguiseManager.addDisguise(owner, new WitchDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_AXE), 0);
        setItem(new ItemStack(Material.IRON_SPADE), 1);
        setItem(new ItemStack(Material.LEASH), 2, getAttributeByClass(BatLeash.class));
        setItem(new ItemStack(Material.NETHER_STAR), 3);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_AXE), 0);
        setItem(new ItemStack(Material.IRON_SPADE), 1);
        setItem(Compass.COMPASS_ITEM, 2);
    }

}
