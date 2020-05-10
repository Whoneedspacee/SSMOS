package SSM.Kits;

import SSM.*;
import SSM.Abilities.*;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Heavyweight;
import SSM.Attributes.Regeneration;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class KitIronGolem extends Kit {

    public KitIronGolem() {
        super();

        this.damage = 7;
        this.armor = 8;
        this.speed = 0.2f;
        this.regeneration = 0.2;
        this.knockback = 1.0;
        this.disguise = DisguiseType.IRON_GOLEM;
        this.name = "Iron_Golem";
        this.menuItem = Material.IRON_BLOCK;
    }

    public void equipKit(Player player) {
        super.equipKit(player);

        setArmor(Material.DIAMOND_BOOTS, 0);
        setArmor(Material.IRON_LEGGINGS, 1);
        setArmor(Material.IRON_CHESTPLATE, 2);
        setArmor(Material.IRON_HELMET, 3);

        setItem(Material.IRON_AXE, 0);
        setItem(Material.IRON_PICKAXE, 1, new IronHook());
        setItem(Material.IRON_SHOVEL, 2, new SeismicSlam());

        addAttribute(new Regeneration(regeneration, 1));
        addAttribute(new GenericDoubleJump(0.61, 0.8, 1, Sound.ENTITY_GHAST_SHOOT));
        addAttribute(new Heavyweight());
    }

}
