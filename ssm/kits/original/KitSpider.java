package ssm.kits.original;

import ssm.abilities.original.Needler;
import ssm.abilities.original.SpinWeb;
import ssm.attributes.*;
import ssm.attributes.doublejumps.custom.SpiderJump;
import ssm.kits.Kit;
import ssm.managers.DisguiseManager;
import ssm.managers.disguises.SpiderDisguise;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
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
        this.podium_mob_type = EntityType.SPIDER;
    }

    @Override
    public void initializeKit() {
        setArmorSlot(Material.IRON_BOOTS, 0);
        setArmorSlot(Material.CHAINMAIL_LEGGINGS, 1);
        setArmorSlot(Material.CHAINMAIL_CHESTPLATE, 2);

        setAbility(new Needler(), 0);
        setAbility(new SpinWeb(), 1);

        addAttribute(new Regeneration(regeneration));
        addAttribute(new Hunger());
        addAttribute(new Compass());
        addAttribute(new SpiderJump(1.0, 1.0, Sound.SPIDER_IDLE));
        addAttribute(new ExpCharge(0.005f, 1, false, false, true));
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
