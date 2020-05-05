package SSM.Kits;

import SSM.Abilities.*;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Attributes.ExpCharge;
import SSM.Attributes.Regeneration;
import SSM.Kit;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitHero extends Kit {

    public KitHero(){
        super();
        this.name = "Hero";
        this.damage = 6;
        this.speed = 0.2f;
        this.armor = 6.0;
        this.knockback = 0;
        this.regeneration = 0.4;
        this.disguise = DisguiseType.ILLUSIONER;
        this.menuItem = new ItemStack(Material.EXPERIENCE_BOTTLE);
    }

    public void equipKit(Player player) {
        super.equipKit(player);

        setArmor(Material.CHAINMAIL_BOOTS, 0);
        setArmor(Material.CHAINMAIL_LEGGINGS, 1);
        setArmor(Material.CHAINMAIL_CHESTPLATE, 2);
        setArmor(Material.CHAINMAIL_HELMET, 3);

        setItem(Material.BOOK, 0, new SpellList());
        addAttribute(new heroFly());
        addAttribute(new heroSpeed());
        addAttribute(new heroTeleport());
        addAttribute(new heroGay());
        addAttribute(new heroFireball());
        addAttribute(new heroHeal());
        addAttribute(new heroParalyze());
        addAttribute(new heroDitto());

        addAttribute(new Regeneration(regeneration, 1));
        addAttribute(new GenericDoubleJump(0.61, 0.8, 1, Sound.ENTITY_GHAST_SHOOT));
        addAttribute(new ExpCharge(0.0045F, 1));

}}
