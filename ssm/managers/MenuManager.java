package ssm.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import ssm.Main;
import ssm.managers.smashmenu.MenuRunnable;
import ssm.managers.smashmenu.SmashMenu;

import java.util.HashMap;

public class MenuManager implements Listener {

    public static MenuManager ourInstance;
    private static JavaPlugin plugin = Main.getInstance();
    private static HashMap<Player, SmashMenu> player_menus = new HashMap<Player, SmashMenu>();

    public MenuManager() {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        ourInstance = this;
    }

    public static SmashMenu createPlayerMenu(Player player, Inventory inventory) {
        SmashMenu menu = new SmashMenu(inventory);
        player_menus.put(player, menu);
        return menu;
    }

    @EventHandler
    public void onPlayerClick(InventoryClickEvent e) {
        if(!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) e.getWhoClicked();
        SmashMenu menu = player_menus.get(player);
        if(menu == null || menu.getInventory() == null || !menu.getInventory().equals(e.getClickedInventory())) {
            return;
        }
        MenuRunnable runnable = menu.getActionFromSlot(e.getRawSlot());
        if(runnable != null) {
            runnable.run(e);
        }
    }

    @EventHandler
    public void onPlayerCloseInventory(InventoryCloseEvent e) {
        if(!(e.getPlayer() instanceof Player)) {
            return;
        }
        Player player = (Player) e.getPlayer();
        player_menus.remove(player);
    }

}
