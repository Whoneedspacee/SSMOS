package SSM;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.Listener;

public class Kit implements Listener {

    // used for finding the kit to equip on command, ex: /kit name
    protected String name;
    protected double damage;
    protected double knockback;
    protected double regeneration;
    protected double speed;

    // list of materials for armor
    protected ItemStack[] armor;

    // list of materials for weapons
    protected ItemStack[] weapons;

    // assigned to the weapons above by index, ex: 1st ability goes on the 1st weapon, etc
    protected Ability[] abilities;

    public Kit() {
        name = "";
        damage = 0;
        knockback = 0;
        regeneration = 0;
        speed = 0;
        armor = new ItemStack[4];
        weapons = new ItemStack[9];
        abilities = new Ability[9];
    }

    public void equipKit(Player player) {
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

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        int selected = player.getInventory().getHeldItemSlot();
        if (!SSM.playerKit.get(player.getUniqueId()).equals(this)) {
            return;
        }
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
