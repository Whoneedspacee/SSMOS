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
    protected double speed = 0;

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
        player.getInventory().clear();
        for (ItemStack item : armor) {
            if (item == null) {
                continue;
            }
            ItemMeta meta = item.getItemMeta();
            meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "generic.attackDamage", damage, AttributeModifier.Operation.ADD_SCALAR));
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

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (player == null) {
            return;
        }
        Kit chosen = SSM.playerKit.get(player.getUniqueId());
        if(chosen == null || !chosen.equals(this)) {
            return;
        }
        int selected = player.getInventory().getHeldItemSlot();
        if (selected >= abilities.length) {
            return;
        }
        Ability using = abilities[selected];
        if (using == null) {
            return;
        }
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            using.activateLeft(player);
        }
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            using.activateRight(player);
        }
    }

}
