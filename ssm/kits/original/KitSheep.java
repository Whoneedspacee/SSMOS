package ssm.kits.original;

import ssm.abilities.original.StaticLaser;
import ssm.abilities.original.WoolMine;
import ssm.abilities.original.WoolyRocket;
import ssm.attributes.Compass;
import ssm.attributes.doublejumps.GenericDoubleJump;
import ssm.attributes.Hunger;
import ssm.attributes.Regeneration;
import ssm.kits.Kit;
import ssm.managers.DisguiseManager;
import ssm.managers.disguises.SheepDisguise;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.ItemStack;

public class KitSheep extends Kit {

    public KitSheep() {
        super();
        this.damage = 5;
        this.armor = 5;
        this.regeneration = 0.25;
        this.knockback = 1.7;
        this.name = "Sir. Sheep";
        this.menuItem = Material.WOOL;
        this.podium_mob_type = EntityType.SHEEP;
    }

    @Override
    public void initializeKit() {
        setArmorSlot(Material.CHAINMAIL_BOOTS, 0);
        setArmorSlot(Material.CHAINMAIL_LEGGINGS, 1);
        setArmorSlot(Material.CHAINMAIL_CHESTPLATE, 2);

        setAbility(new StaticLaser(), 0);
        setAbility(new WoolMine(), 1);
        setAbility(new WoolyRocket(), 2);

        addAttribute(new Regeneration(regeneration));
        addAttribute(new Hunger());
        addAttribute(new Compass());
        addAttribute(new GenericDoubleJump(1, 1, 1, Sound.GHAST_FIREBALL));

        DisguiseManager.addDisguise(owner, new SheepDisguise(owner));
    }

    @Override
    public void setPreviewHotbar() {
        setItem(new ItemStack(Material.IRON_SWORD), 0);
        setItem(new ItemStack(Material.IRON_AXE), 1);
        setItem(new ItemStack(Material.IRON_SPADE), 2);
        setItem(new ItemStack(Material.NETHER_STAR), 3);
    }

    @Override
    public void setGameHotbar() {
        setItem(new ItemStack(Material.IRON_SWORD), 0);
        setItem(new ItemStack(Material.IRON_AXE), 1);
        setItem(new ItemStack(Material.IRON_SPADE), 2);
        setItem(Compass.COMPASS_ITEM, 3);
    }

    @Override
    public Entity getNewPodiumMob(Location spawn_location) {
        Entity entity = super.getNewPodiumMob(spawn_location);
        if(entity instanceof Sheep) {
            Sheep sheep = (Sheep) entity;
            sheep.setColor(DyeColor.WHITE);
        }
        return entity;
    }

}
