package SSM.Kits;

import SSM.*;
import SSM.Abilities.*;
//import SSM.Attributes.ClearProjectile;
import SSM.Attributes.BowCharge.Barrage;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.ItemGenerator;
import SSM.Attributes.Regeneration;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class KitSkeleton extends Kit {

    public KitSkeleton() {
        super();

        this.damage = 4;
        this.armor = 7;
        this.speed = 0.24f;
        this.regeneration = 0.25;
        this.knockback = 1.9;
        this.disguise = DisguiseType.SKELETON;
        this.name = "Skeleton";
        this.menuItem = Material.BOW;
    }

    public void equipKit(Player player) {
        super.equipKit(player);

        setArmor(Material.CHAINMAIL_BOOTS, 3);
        setArmor(Material.CHAINMAIL_LEGGINGS, 2);
        setArmor(Material.CHAINMAIL_CHESTPLATE, 1);
        setArmor(Material.CHAINMAIL_HELMET, 0);

        setItem(Material.IRON_AXE, 0, new BoneExplosion());
        setItem(Material.BOW, 1, new RopedArrow());

        addAttribute(new ItemGenerator(Material.ARROW, 1, 3, 5));
        addAttribute(new Regeneration(regeneration, 1));
        addAttribute(new GenericDoubleJump(0.61, 0.8, 1, Sound.ENTITY_GHAST_SHOOT));
        addAttribute(new Barrage(1.1, 0.25, 5));
    }

}
