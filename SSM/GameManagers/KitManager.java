package SSM.GameManagers;

import SSM.Abilities.Ability;
import SSM.Kits.Kit;
import SSM.SSM;
import SSM.Utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

public class KitManager implements Listener {

    private static KitManager ourInstance;
    private static HashMap<Player, Kit> playerKit = new HashMap<Player, Kit>();
    private JavaPlugin plugin = SSM.getInstance();

    public KitManager() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        ourInstance = this;
    }

    public static void equipPlayer(Player player, Kit check) {
        Utils.fullHeal(player);
        unequipPlayer(player);
        Kit kit = playerKit.get(player);
        try {
            kit = check.getClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (kit != null) {
            kit.setOwner(player);
        }
        playerKit.put(player, kit);
        DisplayManager.buildScoreboard();
    }

    public static void unequipPlayer(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[player.getInventory().getArmorContents().length]);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setWalkSpeed(0.2f);
        Kit kit = playerKit.get(player);
        if (kit == null) {
            return;
        }
        kit.destroyKit();
        playerKit.remove(player);
    }

    public static Ability getCurrentAbility(Player player) {
        Kit kit = KitManager.getPlayerKit(player);
        if (kit == null) {
            return null;
        }
        int currentSlot = player.getInventory().getHeldItemSlot();
        return kit.getAbilityInSlot(currentSlot);
    }

    public static Kit getPlayerKit(Player player) {
        return playerKit.get(player);
    }

    public static List<Kit> getAllKits() {
        return GameManager.getGamemode().getAllowedKits();
    }

    public static KitManager getInstance() {
        return ourInstance;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        playerKit.remove(e.getPlayer());
    }

    @EventHandler
    public void clickEvent(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        if (item == null || item.getItemMeta() == null) {
            return;
        }
        if (e.getView().getTitle().contains("Kit")) {
            for (Kit kit : KitManager.getAllKits()) {
                // Hard Coded Horribleness
                if (item.getItemMeta().getDisplayName().equals(ChatColor.RESET + kit.getName())) {
                    KitManager.equipPlayer(player, kit);
                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1f, 1f);
                    player.closeInventory();
                    break;
                }
            }
            e.setCancelled(true);
        }
    }

}
