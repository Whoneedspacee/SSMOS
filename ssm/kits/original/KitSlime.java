package ssm.kits.original;

import ssm.abilities.original.SlimeRocket;
import ssm.abilities.original.SlimeSlam;
import ssm.attributes.Compass;
import ssm.attributes.doublejumps.GenericDoubleJump;
import ssm.attributes.ExpCharge;
import ssm.attributes.Hunger;
import ssm.attributes.Regeneration;
import ssm.kits.Kit;
import ssm.managers.DisguiseManager;
import ssm.managers.disguises.SlimeDisguise;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class KitSlime extends Kit {

    public KitSlime() {
        super();
        this.damage = 6;
        this.armor = 4;
        this.regeneration = 0.35;
        this.knockback = 1.75;
        this.name = "Slime";
        this.menuItem = Material.SLIME_BALL;
        this.podium_mob_type = EntityType.SLIME;
    }

    @Override
    public void initializeKit() {
        setArmorSlot(Material.CHAINMAIL_BOOTS, 0);
        setArmorSlot(Material.CHAINMAIL_CHESTPLATE, 2);
        setArmorSlot(Material.CHAINMAIL_HELMET, 3);

        setAbility(new SlimeRocket(), 0);
        setAbility(new SlimeSlam(), 1);

        addAttribute(new Regeneration(regeneration));
        addAttribute(new Hunger());
        addAttribute(new Compass());
        addAttribute(new GenericDoubleJump(1.2, 1.0, Sound.GHAST_FIREBALL));
        addAttribute(new ExpCharge(0.00321f, 1, true, true, false));

        DisguiseManager.addDisguise(owner, new SlimeDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_SWORD), 0);
        setItem(new ItemStack(Material.IRON_AXE), 1);
        setItem(new ItemStack(Material.NETHER_STAR), 2);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_SWORD), 0);
        setItem(new ItemStack(Material.IRON_AXE), 1);
        setItem(Compass.COMPASS_ITEM, 2);
    }

}
