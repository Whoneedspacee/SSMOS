package SSM.Kits;

import SSM.Abilities.Needler;
import SSM.Abilities.SpinWeb;
import SSM.Attributes.*;
import SSM.Attributes.DoubleJumps.Custom.SpiderJump;
import SSM.Attributes.DoubleJumps.DirectDoubleJump;
import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.SpiderDisguise;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitSpider extends Kit {

    public KitSpider() {
        super();
        this.damage = 6;
        this.armor = 5.5;
        this.regeneration = 0.25;
        this.knockback = 1.5;
        this.name = "Spider";
        this.menuItem = Material.SPIDER_EYE;
    }

    @Override
    public void initializeKit() {
        setArmor(Material.IRON_BOOTS, 0);
        setArmor(Material.CHAINMAIL_LEGGINGS, 1);
        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);

        setAbility(new Needler(), 0);
        setAbility(new SpinWeb(), 1);

        addAttribute(new Regeneration(regeneration));
        addAttribute(new Hunger());
        addAttribute(new Compass());
        addAttribute(new SpiderJump(1.0, 1.0, 1, Sound.SPIDER_IDLE));
        addAttribute(new ExpCharge(0.005f, 1, false, false));
        addAttribute(new Climb(0.2));

        DisguiseManager.addDisguise(owner, new SpiderDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_SWORD), 0);
        setItem(new ItemStack(Material.IRON_AXE), 1);
        setItem(new ItemStack(Material.SPIDER_EYE), 2, getAttributeByClass(SpiderJump.class));
        setItem(new ItemStack(Material.FERMENTED_SPIDER_EYE), 3, getAttributeByClass(Climb.class));
        setItem(new ItemStack(Material.NETHER_STAR), 4);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_SWORD), 0);
        setItem(new ItemStack(Material.IRON_AXE), 1);
        setItem(Compass.COMPASS_ITEM, 2);
    }

}
