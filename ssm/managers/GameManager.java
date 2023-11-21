package ssm.managers;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import ssm.Main;
import ssm.managers.gamemodes.SmashGamemode;
import ssm.managers.gamemodes.SoloGamemode;
import ssm.managers.gamemodes.TestingGamemode;
import ssm.managers.smashmenu.SmashMenu;
import ssm.managers.smashserver.SmashServer;
import ssm.kits.Kit;
import ssm.utilities.ServerMessageType;
import ssm.utilities.Utils;

import java.util.ArrayList;
import java.util.List;

public class GameManager implements Listener {

    private static JavaPlugin plugin = Main.getInstance();
    public static List<SmashServer> servers = new ArrayList<SmashServer>();
    public static GameManager ourInstance;

    public GameManager() {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        ourInstance = this;
    }

    public static SmashServer createSmashServer(SmashGamemode gamemode) {
        if(gamemode == null) {
            gamemode = new SoloGamemode();
        }
        SmashServer server = new SmashServer();
        server.setCurrentGamemode(gamemode);
        servers.add(server);
        return server;
    }

    public static void deleteSmashServer(SmashServer server) {
        if(server.getLobbyMap() != null) {
            server.getLobbyMap().deleteWorld();
        }
        server.getCurrentGamemode().deleteMaps();
        servers.remove(server);
    }

    public static SmashServer getPlayerServer(Player player) {
        for(SmashServer server : servers) {
            if(server.players.contains(player)) {
                return server;
            }
        }
        return null;
    }

    public static void setDefaultKit(Player player, Kit kit) {
        SmashServer server = getPlayerServer(player);
        if(server == null) {
            return;
        }
        Utils.sendServerMessageToPlayer("Set " + ChatColor.GREEN + kit.getName() +
                ChatColor.GRAY + " as your default kit.", player, ServerMessageType.GAME);
        ConfigManager.setPlayerGamemodeConfigOption(player, ConfigManager.ConfigOption.DEFAULT_KIT, kit.getName(), server.getCurrentGamemode());
    }

    public static Kit getDefaultKit(Player player) {
        SmashServer server = getPlayerServer(player);
        if(server == null) {
            return null;
        }
        SmashGamemode gamemode = server.getCurrentGamemode();
        if (gamemode instanceof TestingGamemode) {
            gamemode = new SoloGamemode();
        }
        Object config_option = ConfigManager.getPlayerGamemodeConfigOption(player, ConfigManager.ConfigOption.DEFAULT_KIT, gamemode);
        if (config_option == null) {
            return null;
        }
        String default_kit_name = config_option.toString();
        for (Kit kit : server.getCurrentGamemode().getAllowedKits()) {
            if (kit.getName().equals(default_kit_name)) {
                return kit;
            }
        }
        return null;
    }

    public static void teleportPlayerToLobby(Player player) {
        Location location = Bukkit.getWorlds().get(0).getSpawnLocation();
        player.teleport(location);
    }

    public static void teleportAllPlayersToLobby() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            teleportPlayerToLobby(player);
        }
    }

    public static void openServerMenu(Player player) {
        int slot = 0;
        Inventory selectServer = Bukkit.createInventory(player, 9 * 2, "Choose a Server");
        SmashMenu menu = MenuManager.createPlayerMenu(player, selectServer);
        for (SmashServer server : servers) {
            Material server_material = Material.EMERALD_BLOCK;
            if(server.isStarting() || server.isPlaying()) {
                server_material = Material.GOLD_BLOCK;
            }
            if(server.players.size() >= server.getCurrentGamemode().max_players) {
                server_material = Material.REDSTONE_BLOCK;
            }
            ItemStack item = new ItemStack(server_material);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(ChatColor.RESET + server.toString() + " " + server.players.size() + "/" + server.getCurrentGamemode().max_players);
            item.setItemMeta(itemMeta);
            selectServer.setItem(slot, item);
            menu.setActionFromSlot(slot, (e) -> {
                if(e.getWhoClicked() instanceof Player) {
                    server.teleportToServer((Player) e.getWhoClicked());
                }
            });
            slot++;
        }
        player.openInventory(selectServer);
    }

    @EventHandler
    public void PlayerJoin(PlayerJoinEvent e) {
        teleportPlayerToLobby(e.getPlayer());
        KitManager.unequipPlayer(e.getPlayer());
        e.getPlayer().setHealth(e.getPlayer().getMaxHealth());
        e.getPlayer().setFoodLevel(20);
        e.getPlayer().setGameMode(GameMode.ADVENTURE);
    }

    @EventHandler
    public void cancelChickenEggSpawn(CreatureSpawnEvent e) {
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.EGG) {
            e.setCancelled(true);
        }
    }

}
