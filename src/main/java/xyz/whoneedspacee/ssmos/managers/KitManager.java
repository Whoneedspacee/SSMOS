package xyz.whoneedspacee.ssmos.managers;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import xyz.whoneedspacee.ssmos.managers.gamestate.GameState;
import xyz.whoneedspacee.ssmos.managers.smashmenu.SmashMenu;
import xyz.whoneedspacee.ssmos.abilities.Ability;
import xyz.whoneedspacee.ssmos.kits.Kit;
import xyz.whoneedspacee.ssmos.Main;
import xyz.whoneedspacee.ssmos.managers.smashserver.SmashServer;
import xyz.whoneedspacee.ssmos.utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

public class KitManager implements Listener {

    private static KitManager ourInstance;
    private static HashMap<Player, Kit> playerKit = new HashMap<Player, Kit>();
    private JavaPlugin plugin = Main.getInstance();

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
    }

    public static void unequipPlayer(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[player.getInventory().getArmorContents().length]);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setWalkSpeed(0.2f);
        for(PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
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

    public static KitManager getInstance() {
        return ourInstance;
    }

    public static void openKitMenu(Player player) {
        SmashServer server = GameManager.getPlayerServer(player);
        if(server == null) {
            return;
        }
        int slot = 10;
        int count = 0;
        List<Kit> allowed_kits = server.getCurrentGamemode().getAllowedKits();
        Inventory selectKit = Bukkit.createInventory(player, (int) (9 * (Math.ceil(allowed_kits.size() / 7.0) + 2)), "Choose a Kit");
        SmashMenu menu = MenuManager.createPlayerMenu(player, selectKit);
        for (Kit kit : allowed_kits) {
            ItemStack item = kit.getMenuItemStack();
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(ChatColor.RESET + kit.getName());
            item.setItemMeta(itemMeta);
            selectKit.setItem(slot, item);
            menu.setActionFromSlot(slot, (e) -> {
                if(e.getWhoClicked() instanceof Player) {
                    Player clicked = (Player) e.getWhoClicked();
                    if(server.getState() <= GameState.LOBBY_VOTING) {
                        server.pre_selected_kits.put(clicked, kit);
                    } else {
                        KitManager.equipPlayer(clicked, kit);
                    }
                    clicked.playSound(clicked.getLocation(), Sound.ORB_PICKUP, 1f, 1f);
                    clicked.closeInventory();
                }
            });
            slot++;
            count++;

            if (count % 7 == 0) {
                slot += 2;
            }
        }
        player.openInventory(selectKit);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        playerKit.remove(e.getPlayer());
    }

}
