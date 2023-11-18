package ssm.managers;

import org.bukkit.ChatColor;
import ssm.managers.gamemodes.SmashGamemode;
import ssm.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager implements Listener {

    // This manager was thrown together, if you want more config options please make this better
    public enum ConfigOption {
        DEFAULT_KIT("default-kit"),
        BOSS_KIT("preferred-boss-kit"),
        PODIUM_LOCATION("podium-locations");

        private String message;

        ConfigOption(String message) {
            this.message = message;
        }

        public String toString() {
            return message;
        }
    }

    public static ConfigManager ourInstance;
    private static JavaPlugin plugin = Main.getInstance();

    public ConfigManager() {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        ourInstance = this;
    }

    public static void setConfigOption(ConfigOption option, Object data) {
        setConfigOption(option, data, null);
    }

    public static Object getConfigOption(ConfigOption option) {
        return getConfigOption(option, null);
    }

    public static void setConfigOption(ConfigOption option, Object data, String identifier) {
        String storage_location = option.toString();
        if(identifier != null) {
            storage_location += "." + identifier;
        }
        Main.getInstance().getConfig().set(storage_location, data);
        Main.getInstance().saveConfig();
    }

    public static Object getConfigOption(ConfigOption option, String identifier) {
        String storage_location = option.toString();
        if(identifier != null) {
            storage_location += "." + identifier;
        }
        return Main.getInstance().getConfig().get(storage_location);
    }

    public static void setPlayerConfigOption(Player player, ConfigOption option, Object data) {
        String storage_location = "players." + player.getUniqueId() + "." + option;
        Main.getInstance().getConfig().set(storage_location, data);
        Main.getInstance().saveConfig();
    }

    public static Object getPlayerConfigOption(Player player, ConfigOption option) {
        String storage_location = "players." + player.getUniqueId() + "." + option;
        return Main.getInstance().getConfig().get(storage_location);
    }

    public static void setPlayerGamemodeConfigOption(Player player, ConfigOption option, Object data) {
        setPlayerGamemodeConfigOption(player, option, data, null);
    }

    public static Object getPlayerGamemodeConfigOption(Player player, ConfigOption option) {
        return getPlayerGamemodeConfigOption(player, option, null);
    }

    public static void setPlayerGamemodeConfigOption(Player player, ConfigOption option, Object data, SmashGamemode gamemode) {
        if(gamemode == null) {
            Bukkit.broadcastMessage(ChatColor.RED + "Tried to set null gamemode config option");
            return;
        }
        String storage_location = "players." + player.getUniqueId() + "." + option + "." + gamemode.getName();
        Main.getInstance().getConfig().set(storage_location, data);
        Main.getInstance().saveConfig();
    }

    public static Object getPlayerGamemodeConfigOption(Player player, ConfigOption option, SmashGamemode gamemode) {
        if(gamemode == null) {
            Bukkit.broadcastMessage(ChatColor.RED + "Tried to get null gamemode config option");
            return null;
        }
        String storage_location = "players." + player.getUniqueId() + "." + option + "." + gamemode.getName();
        return Main.getInstance().getConfig().get(storage_location);
    }

}
