package ssm.managers;

import ssm.managers.teams.SmashTeam;
import ssm.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class TeamManager implements Listener {

    public static TeamManager ourInstance;
    private static JavaPlugin plugin = Main.getInstance();
    private static List<SmashTeam> teams = new ArrayList<SmashTeam>();

    public TeamManager() {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        ourInstance = this;
    }

    public static SmashTeam createTeam(String team_name, ChatColor team_color) {
        SmashTeam team = new SmashTeam(team_name, team_color);
        teams.add(team);
        return team;
    }

    public static void removeTeam(SmashTeam team) {
        teams.remove(team);
    }

    public static SmashTeam getPlayerTeam(Player player) {
        for (SmashTeam team : teams) {
            if (team.isOnTeam(player)) {
                return team;
            }
        }
        return null;
    }

    public enum TeamColor {
        YELLOW(ChatColor.YELLOW, new String[]{"Banana", "Mopple", "Custard", "Sponge", "Star", "Giraffe", "Lego", "Light"}),
        GREEN(ChatColor.GREEN, new String[]{"Creepers", "Alien", "Seaweed", "Emerald", "Grinch", "Shrub", "Snake", "Leaf"}),
        AQUA(ChatColor.AQUA, new String[]{"Diamond", "Ice", "Pool", "Kraken", "Aquatic", "Ocean"}),
        RED(ChatColor.RED, new String[]{"Heart", "Tomato", "Ruby", "Jam", "Rose", "Apple", "TNT"}),
        GOLD(ChatColor.GOLD, new String[]{"Mango", "Foxes", "Sunset", "Nuggets", "Lion", "Desert", "Gapple"}),
        LIGHT_PURPLE(ChatColor.LIGHT_PURPLE, new String[]{"Dream", "Cupcake", "Cake", "Candy", "Unicorn"}),
        DARK_BLUE(ChatColor.DARK_BLUE, new String[]{"Squid", "Lapis", "Sharks", "Galaxy", "Empoleon"}),
        DARK_RED(ChatColor.DARK_RED, new String[]{"Rose", "Apple", "Twizzler", "Rocket", "Blood"}),
        WHITE(ChatColor.WHITE, new String[]{"Ghosts", "Spookies", "Popcorn", "Seagull", "Rice", "Snowman", "Artic"}),
        BLUE(ChatColor.BLUE, new String[]{"Sky", "Whale", "Lake", "Birds", "Bluebird", "Piplup"}),
        DARK_GREEN(ChatColor.DARK_GREEN, new String[]{"Forest", "Zombies", "Cactus", "Slime", "Toxic", "Poison"}),
        DARK_PURPLE(ChatColor.DARK_PURPLE, new String[]{"Amethyst", "Slugs", "Grape", "Witch", "Magic", "Zula"}),
        DARK_AQUA(ChatColor.DARK_AQUA, new String[]{"Snorlax", "Aquatic", "Clam", "Fish"});

        public static TeamColor first() {
            return TeamColor.values()[0];
        }

        public final ChatColor color;
        public final String[] names;

        TeamColor(ChatColor color, String[] names) {
            this.color = color;
            this.names = names;
        }

        public TeamColor next() {
            if (ordinal() == values().length - 1) {
                return null;
            }
            return values()[ordinal() + 1];
        }
    }

}
