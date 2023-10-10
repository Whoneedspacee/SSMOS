package SSM.Kits;

import SSM.Abilities.Explode;
import SSM.Abilities.SulphurBomb;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.Hunger;
import SSM.Attributes.LightningShield;
import SSM.Attributes.Regeneration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class KitCreeper extends Kit {

    public KitCreeper() {
        super();
        this.damage = 6;
        this.armor = 4;
        this.regeneration = 0.4;
        this.knockback = 1.65;
        this.name = "Creeper";
        this.menuItem = Material.SULPHUR;
    }

    public void equipKit(Player player) {
        super.equipKit(player);
        setArmor(Material.IRON_BOOTS, 0);
        setArmor(Material.LEATHER_LEGGINGS, 1);
        setArmor(Material.LEATHER_CHESTPLATE, 2);
        setArmor(Material.LEATHER_HELMET, 3);

        setItem(Material.IRON_AXE, 0, new SulphurBomb());
        setItem(Material.IRON_SPADE, 1, new Explode());

        addAttribute(new Regeneration(regeneration, 20));
        addAttribute(new GenericDoubleJump(0.9, 0.9, 1, Sound.GHAST_FIREBALL));
        //addAttribute(new Hunger(10));
        addAttribute(new LightningShield());
    }
}
