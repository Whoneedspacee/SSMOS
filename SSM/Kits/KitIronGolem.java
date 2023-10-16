package SSM.Kits;

import SSM.Abilities.Fissure;
import SSM.Abilities.IronHook;
import SSM.Abilities.SeismicSlam;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Potion;
import SSM.Attributes.Regeneration;
import SSM.GameManagers.Disguises.IronGolemDisguise;
import SSM.GameManagers.DisguiseManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class KitIronGolem extends Kit {

    public KitIronGolem() {
        super();
        this.damage = 7;
        this.armor = 8;
        this.regeneration = 0.2;
        this.knockback = 1.0;
        this.name = "Iron Golem";
        this.menuItem = Material.IRON_BLOCK;
    }

    public void equipKit(Player player) {
        super.equipKit(player);

        setArmor(Material.DIAMOND_BOOTS, 0);
        setArmor(Material.IRON_LEGGINGS, 1);
        setArmor(Material.IRON_CHESTPLATE, 2);
        setArmor(Material.IRON_HELMET, 3);

        setItem(Material.IRON_AXE, 0, new Fissure());
        setItem(Material.IRON_PICKAXE, 1, new IronHook());
        setItem(Material.IRON_SPADE, 2, new SeismicSlam());

        addAttribute(new Regeneration(regeneration, 20));
        addAttribute(new GenericDoubleJump(0.9, 0.9, 1, Sound.GHAST_FIREBALL));
        addAttribute(new Potion(PotionEffectType.SLOW, 1));

        DisguiseManager.addDisguise(owner, new IronGolemDisguise(owner));
    }

}
