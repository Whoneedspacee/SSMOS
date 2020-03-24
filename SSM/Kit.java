package SSM;

import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class Kit implements Listener {

    /*  You must add your new kits to the allkits list in the main SSM.java file! */

    // used for finding the kit to equip on command, ex: /kit name
    protected String name = "";
    protected double damage = 0;
    protected double knockback = 0;
    protected double regeneration = 0;
    protected float speed = 0f;

    // list of materials for armor
    protected ItemStack[] armor = new ItemStack[4];

    // list of materials for weapons
    protected ItemStack[] weapons = new ItemStack[9];

    // assigned to the weapons above by index, ex: 1st ability goes on the 1st weapon, etc
    protected Ability[] abilities = new Ability[9];

    protected Plugin plugin;

    public Kit(Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void equipKit(Player player) {
        player.setWalkSpeed(speed);
        player.getInventory().clear();
        for (ItemStack item : armor) {
            if (item == null) {
                continue;
            }
            ItemMeta meta = item.getItemMeta();
            meta.setUnbreakable(true);
            item.setItemMeta(meta);


        }
        player.getInventory().setArmorContents(armor);
        for (int i = 0; i < weapons.length; i++) {
            ItemStack item = weapons[i];
            Ability ability = abilities[i];
            if (item == null) {
                continue;
            }
            ItemMeta meta = item.getItemMeta();
            if (ability != null) {
                meta.setDisplayName(ability.name);
            }
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
            player.getInventory().addItem(item);
        }
    }

    public String getName() {
        return name;
    }

    public Ability[] getAbilities() {
        return abilities;
    }


}
