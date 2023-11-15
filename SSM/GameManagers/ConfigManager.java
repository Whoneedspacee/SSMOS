package SSM.GameManagers;

import SSM.GameManagers.Gamemodes.SmashGamemode;
import SSM.SSM;
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
    private static JavaPlugin plugin = SSM.getInstance();

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
        SSM.getInstance().getConfig().set(storage_location, data);
        SSM.getInstance().saveConfig();
    }

    public static Object getConfigOption(ConfigOption option, String identifier) {
        String storage_location = option.toString();
        if(identifier != null) {
            storage_location += "." + identifier;
        }
        return SSM.getInstance().getConfig().get(storage_location);
    }

    public static void setPlayerConfigOption(Player player, ConfigOption option, Object data) {
        String storage_location = "players." + player.getUniqueId() + "." + option;
        SSM.getInstance().getConfig().set(storage_location, data);
        SSM.getInstance().saveConfig();
    }

    public static Object getPlayerConfigOption(Player player, ConfigOption option) {
        String storage_location = "players." + player.getUniqueId() + "." + option;
        return SSM.getInstance().getConfig().get(storage_location);
    }

    public static void setPlayerGamemodeConfigOption(Player player, ConfigOption option, Object data) {
        setPlayerGamemodeConfigOption(player, option, data, null);
    }

    public static Object getPlayerGamemodeConfigOption(Player player, ConfigOption option) {
        return getPlayerGamemodeConfigOption(player, option, null);
    }

    public static void setPlayerGamemodeConfigOption(Player player, ConfigOption option, Object data, SmashGamemode gamemode) {
        if(gamemode == null) {
            gamemode = GameManager.getCurrentGamemode();
        }
        String storage_location = "players." + player.getUniqueId() + "." + option + "." + gamemode.getName();
        SSM.getInstance().getConfig().set(storage_location, data);
        SSM.getInstance().saveConfig();
    }

    public static Object getPlayerGamemodeConfigOption(Player player, ConfigOption option, SmashGamemode gamemode) {
        if(gamemode == null) {
            gamemode = GameManager.getCurrentGamemode();
        }
        String storage_location = "players." + player.getUniqueId() + "." + option + "." + gamemode.getName();
        return SSM.getInstance().getConfig().get(storage_location);
    }

}
