package ssm.kits.boss;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import ssm.Main;
import ssm.abilities.boss.WitherArmy;
import ssm.abilities.boss.WitherSkullBarrage;
import ssm.attributes.*;
import ssm.kits.Kit;
import ssm.managers.DisguiseManager;
import ssm.managers.disguises.WitherDisguise;
import ssm.utilities.Utils;

public class KitWitherBoss extends Kit implements BossKitData {

    public KitWitherBoss() {
        super();
        this.damage = 1;
        this.armor = Utils.getArmorForExactHP(160);
        this.regeneration = 0;
        this.knockback = 0.5;
        this.name = "Wither";
        this.menuItem = Material.SOUL_SAND;
        this.podium_mob_type = EntityType.WITHER;
    }

    @Override
    public void initializeKit() {
        setArmorSlot(Material.DIAMOND_BOOTS, 0);
        setArmorSlot(Material.DIAMOND_LEGGINGS, 1);
        setArmorSlot(Material.DIAMOND_CHESTPLATE, 2);
        setArmorSlot(Material.DIAMOND_HELMET, 3);

        setAbility(new WitherSkullBarrage(), 0);
        setAbility(new WitherArmy(), 1);

        addAttribute(new Regeneration(regeneration));
        addAttribute(new Hunger());
        addAttribute(new Compass());
        addAttribute(new EnergyFlight(0.01f, 0L));
        addAttribute(new ExpCharge(0.005f, 1, true, true, true));
        addAttribute(new NoVoidDeath(2.0, true));
        addAttribute(new NoTrueDamage(3.0));

        DisguiseManager.addDisguise(owner, new WitherDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_AXE), 0);
        setItem(new ItemStack(Material.IRON_PICKAXE), 1);
        setItem(new ItemStack(Material.COMMAND), 2, getAttributeByClass(EnergyFlight.class));
        setItem(new ItemStack(Material.NETHER_STAR), 3);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_AXE), 0);
        setItem(new ItemStack(Material.IRON_PICKAXE), 1);
        setItem(Compass.COMPASS_ITEM, 2);
    }

    @Override
    public double getHealthPerPlayer() {
        return 160;
    }

}
