package SSM.Kits;

import SSM.Abilities.BileBlaster;
import SSM.Attributes.BowCharge.DamageBoost;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.ItemGenerator;
import SSM.Attributes.Regeneration;
import SSM.Kit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class KitZombie extends Kit {

    public KitZombie() {
        super();
        this.damage = 5;
        this.armor = 5;
        this.speed = 0.2f;
        this.regeneration = 0.25;
        this.knockback = 1.4;
        this.name = "Zombie";
        this.menuItem = Material.ROTTEN_FLESH;
    }

    public void equipKit(Player player) {
        super.equipKit(player);

        setArmor(Material.CHAINMAIL_BOOTS, 0);
        setArmor(Material.CHAINMAIL_LEGGINGS, 1);
        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);


        setItem(Material.IRON_AXE, 0, new BileBlaster());
        setItem(Material.BOW, 1);

        addAttribute(new ItemGenerator(Material.ARROW, 1, 2, 3));
        addAttribute(new Regeneration(regeneration, 1));
        addAttribute(new GenericDoubleJump(0.61, 0.8, 1, Sound.ENTITY_GHAST_SHOOT));
        addAttribute(new DamageBoost(1.1, 0.25, 5));
    }
}
