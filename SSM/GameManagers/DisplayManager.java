package SSM.GameManagers;

import SSM.Kits.Kit;
import SSM.SSM;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

import java.util.HashMap;

public class DisplayManager implements Listener {

    private static DisplayManager ourInstance;
    private static JavaPlugin plugin = SSM.getInstance();
    //private static HashMap<Player, Scoreboard> scoreboards = new HashMap<Player, Scoreboard>();

    public DisplayManager() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        ourInstance = this;
        buildScoreboard();
    }

    public static void buildScoreboard() {
        if(GameManager.getState() == GameManager.GameState.LOBBY_WAITING) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                Objective obj = scoreboard.registerNewObjective("menu", "dummy");
                Kit kit = KitManager.getPlayerKit(player);
                String kit_name = "None";
                if(kit != null) {
                    kit_name = kit.getName();
                }
                obj.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Waiting for players...");
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);
                obj.getScore("").setScore(12);
                obj.getScore(ChatColor.YELLOW + "" + ChatColor.BOLD + "Players").setScore(11);
                obj.getScore(GameManager.getTotalPlayers() + "/4").setScore(10);
                obj.getScore(ChatColor.RED + "").setScore(9);
                obj.getScore(ChatColor.RED + "" + ChatColor.BOLD + "Game").setScore(8);
                obj.getScore("Super Smash Mobs").setScore(7);
                obj.getScore(ChatColor.GREEN + "").setScore(6);
                obj.getScore(ChatColor.GOLD + "" + ChatColor.BOLD + "Kit").setScore(5);
                obj.getScore(kit_name).setScore(4);
                obj.getScore(ChatColor.YELLOW + "").setScore(3);
                obj.getScore(ChatColor.AQUA + "" + ChatColor.BOLD + "Server").setScore(2);
                obj.getScore("SSM-1").setScore(1);
                player.setScoreboard(scoreboard);
            }
            return;
        }
        if(GameManager.getState() == GameManager.GameState.LOBBY_VOTING) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                Objective obj = scoreboard.registerNewObjective("menu", "dummy");
                Kit kit = KitManager.getPlayerKit(player);
                String kit_name = "None";
                if(kit != null) {
                    kit_name = kit.getName();
                }
                obj.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Voting ends in " + GameManager.getTimeLeft() + " seconds");
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);
                obj.getScore("").setScore(12);
                obj.getScore(ChatColor.YELLOW + "" + ChatColor.BOLD + "Players").setScore(11);
                obj.getScore(GameManager.getTotalPlayers() + "/4").setScore(10);
                obj.getScore(ChatColor.RED + "").setScore(9);
                obj.getScore(ChatColor.RED + "" + ChatColor.BOLD + "Game").setScore(8);
                obj.getScore("Super Smash Mobs").setScore(7);
                obj.getScore(ChatColor.GREEN + "").setScore(6);
                obj.getScore(ChatColor.GOLD + "" + ChatColor.BOLD + "Kit").setScore(5);
                obj.getScore(kit_name).setScore(4);
                obj.getScore(ChatColor.YELLOW + "").setScore(3);
                obj.getScore(ChatColor.AQUA + "" + ChatColor.BOLD + "Server").setScore(2);
                obj.getScore("SSM-1").setScore(1);
                player.setScoreboard(scoreboard);
            }
            return;
        }
        if(GameManager.getState() == GameManager.GameState.LOBBY_STARTING) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                Objective obj = scoreboard.registerNewObjective("menu", "dummy");
                Kit kit = KitManager.getPlayerKit(player);
                String kit_name = "None";
                if(kit != null) {
                    kit_name = kit.getName();
                }
                obj.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Starting in " + GameManager.getTimeLeft() + " seconds");
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);
                obj.getScore("").setScore(12);
                obj.getScore(ChatColor.YELLOW + "" + ChatColor.BOLD + "Players").setScore(11);
                obj.getScore(GameManager.getTotalPlayers() + "/4").setScore(10);
                obj.getScore(ChatColor.RED + "").setScore(9);
                obj.getScore(ChatColor.RED + "" + ChatColor.BOLD + "Game").setScore(8);
                obj.getScore("Super Smash Mobs").setScore(7);
                obj.getScore(ChatColor.GREEN + "").setScore(6);
                obj.getScore(ChatColor.GOLD + "" + ChatColor.BOLD + "Kit").setScore(5);
                obj.getScore(kit_name).setScore(4);
                obj.getScore(ChatColor.YELLOW + "").setScore(3);
                obj.getScore(ChatColor.AQUA + "" + ChatColor.BOLD + "Server").setScore(2);
                obj.getScore("SSM-1").setScore(1);
                player.setScoreboard(scoreboard);
            }
            return;
        }
        if(GameManager.getState() == GameManager.GameState.GAME_STARTING) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                Objective obj = scoreboard.registerNewObjective("menu", "dummy");
                Kit kit = KitManager.getPlayerKit(player);
                String kit_name = "None";
                if(kit != null) {
                    kit_name = kit.getName();
                }
                obj.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "SSMOS");
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);
                obj.getScore("").setScore(4);
                obj.getScore(ChatColor.YELLOW + "" + ChatColor.BOLD + "Players").setScore(3);
                obj.getScore(GameManager.getTotalPlayers() + " Players").setScore(2);
                obj.getScore(ChatColor.RED + "").setScore(1);
                player.setScoreboard(scoreboard);
            }
            return;
        }
        if(GameManager.getState() == GameManager.GameState.GAME_PLAYING) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                Objective obj = scoreboard.registerNewObjective("menu", "dummy");
                Kit kit = KitManager.getPlayerKit(player);
                String kit_name = "None";
                if(kit != null) {
                    kit_name = kit.getName();
                }
                obj.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "SSMOS");
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);
                int score = 1;
                obj.getScore("").setScore(score++);
                for(Player add : GameManager.getAllLives().keySet()) {
                    obj.getScore(GameManager.getLives(add) + " " + getLivesColor(add) + add.getName()).setScore(score++);
                }
                obj.getScore(ChatColor.RED + "").setScore(score++);
                obj.getScore(ChatColor.YELLOW + "" + ChatColor.BOLD + "Players").setScore(score++);
                obj.getScore(ChatColor.GREEN + "").setScore(score++);
                player.setScoreboard(scoreboard);
            }
            return;
        }
        if(GameManager.getState() == GameManager.GameState.GAME_ENDING) {
            return;
        }
    }

    public static ChatColor getLivesColor(Player player) {
        return getLivesColor(GameManager.getLives(player));
    }

    public static ChatColor getLivesColor(int lives) {
        if(lives >= 4) {
            return ChatColor.GREEN;
        }
        else if(lives == 3) {
            return ChatColor.YELLOW;
        }
        else if(lives == 2) {
            return ChatColor.GOLD;
        }
        return ChatColor.RED;
    }

}
