package SSM.Kits;

import SSM.Abilities.BileBlaster;
import SSM.Attributes.BowCharge.DamageBoost;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.ItemGenerator;
import SSM.Attributes.Regeneration;
import SSM.GameManagers.Disguises.ZombieDisguise;
import SSM.GameManagers.DisguiseManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class KitZombie extends Kit {

    public KitZombie() {
        super();
        this.damage = 5;
        this.armor = 5;
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
        addAttribute(new Regeneration(regeneration, 20));
        addAttribute(new GenericDoubleJump(0.9, 0.9, 1, Sound.GHAST_FIREBALL));
        addAttribute(new DamageBoost(1.1, 0.25, 5));

        DisguiseManager.addDisguise(owner, new ZombieDisguise(owner));
    }
}
