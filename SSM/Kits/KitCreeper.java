package SSM.Kits;

import SSM.Abilities.Explode;
import SSM.Abilities.SulphurBomb;
import SSM.Attributes.Compass;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Hunger;
import SSM.Attributes.LightningShield;
import SSM.Attributes.Regeneration;
import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.CreeperDisguise;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class KitCreeper extends Kit {

    public KitCreeper() {
        super();
        this.damage = 6;
        this.armor = 4;
        this.regeneration = 0.4;
        this.knockback = 1.65;
        this.name = "Creeper";
        this.menuItem = Material.SULPHUR;
        this.podium_mob_type = EntityType.CREEPER;
    }

    @Override
    public void initializeKit() {
        setArmorSlot(Material.IRON_BOOTS, 0);
        setArmorSlot(Material.LEATHER_LEGGINGS, 1);
        setArmorSlot(Material.LEATHER_CHESTPLATE, 2);
        setArmorSlot(Material.LEATHER_HELMET, 3);

        setAbility(new SulphurBomb(), 0);
        setAbility(new Explode(), 1);

        addAttribute(new Regeneration(regeneration));
        addAttribute(new Hunger());
        addAttribute(new Compass());
        addAttribute(new GenericDoubleJump(0.9, 0.9, 1, Sound.GHAST_FIREBALL));
        addAttribute(new LightningShield());

        DisguiseManager.addDisguise(owner, new CreeperDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_AXE), 0);
        setItem(new ItemStack(Material.IRON_SPADE), 1);
        setItem(new ItemStack(Material.COAL), 2, getAttributeByClass(LightningShield.class));
        setItem(new ItemStack(Material.NETHER_STAR), 3);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_AXE), 0);
        setItem(new ItemStack(Material.IRON_SPADE), 1);
        setItem(Compass.COMPASS_ITEM, 2);
    }

}
