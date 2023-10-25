package SSM.GameManagers;

import SSM.Kits.Kit;
import SSM.SSM;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

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
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Objective obj = scoreboard.getObjective("menu");
        if(obj != null) {
            obj.unregister();
        }
        scoreboard.registerNewObjective("menu", "dummy");
        if (GameManager.getState() == GameManager.GameState.LOBBY_WAITING) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Kit kit = KitManager.getPlayerKit(player);
                String kit_name = "None";
                if (kit != null) {
                    kit_name = kit.getName();
                }
                obj.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Waiting for players...");
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
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);
                player.setScoreboard(scoreboard);
            }
            return;
        }
        if (GameManager.getState() == GameManager.GameState.LOBBY_VOTING) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Kit kit = KitManager.getPlayerKit(player);
                String kit_name = "None";
                if (kit != null) {
                    kit_name = kit.getName();
                }
                obj.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Voting ends in " + GameManager.getTimeLeft() + " seconds");
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
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);
                player.setScoreboard(scoreboard);
            }
            return;
        }
        if (GameManager.getState() == GameManager.GameState.LOBBY_STARTING) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Kit kit = KitManager.getPlayerKit(player);
                String kit_name = "None";
                if (kit != null) {
                    kit_name = kit.getName();
                }
                obj.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Starting in " + GameManager.getTimeLeft() + " seconds");
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
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);
                player.setScoreboard(scoreboard);
            }
            return;
        }
        if (GameManager.getState() == GameManager.GameState.GAME_STARTING) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Kit kit = KitManager.getPlayerKit(player);
                String kit_name = "None";
                if (kit != null) {
                    kit_name = kit.getName();
                }
                obj.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "SSMOS");
                obj.getScore("").setScore(4);
                obj.getScore(ChatColor.YELLOW + "" + ChatColor.BOLD + "Players").setScore(3);
                obj.getScore(GameManager.getTotalPlayers() + " Players").setScore(2);
                obj.getScore(ChatColor.RED + "").setScore(1);
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);
                player.setScoreboard(scoreboard);
            }
            return;
        }
        if (GameManager.getState() == GameManager.GameState.GAME_PLAYING) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Kit kit = KitManager.getPlayerKit(player);
                String kit_name = "None";
                if (kit != null) {
                    kit_name = kit.getName();
                }
                obj.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "SSMOS");
                int score = 1;
                obj.getScore("").setScore(score++);
                // This is terrible forgive my laziness
                for (Player add : GameManager.getAllLives().keySet()) {
                    if(GameManager.getLives(add) != 1) {
                        continue;
                    }
                    obj.getScore(GameManager.getLives(add) + " " + getLivesColor(add) + add.getName()).setScore(score++);
                }
                for (Player add : GameManager.getAllLives().keySet()) {
                    if(GameManager.getLives(add) != 2) {
                        continue;
                    }
                    obj.getScore(GameManager.getLives(add) + " " + getLivesColor(add) + add.getName()).setScore(score++);
                }
                for (Player add : GameManager.getAllLives().keySet()) {
                    if(GameManager.getLives(add) != 3) {
                        continue;
                    }
                    obj.getScore(GameManager.getLives(add) + " " + getLivesColor(add) + add.getName()).setScore(score++);
                }
                for (Player add : GameManager.getAllLives().keySet()) {
                    if(GameManager.getLives(add) != 4) {
                        continue;
                    }
                    obj.getScore(GameManager.getLives(add) + " " + getLivesColor(add) + add.getName()).setScore(score++);
                }
                obj.getScore(ChatColor.RED + "").setScore(score++);
                obj.getScore(ChatColor.YELLOW + "" + ChatColor.BOLD + "Players").setScore(score++);
                obj.getScore(ChatColor.GREEN + "").setScore(score++);
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);
                player.setScoreboard(scoreboard);
            }
            return;
        }
        if (GameManager.getState() == GameManager.GameState.GAME_ENDING) {
            return;
        }
    }

    public static ChatColor getLivesColor(Player player) {
        return getLivesColor(GameManager.getLives(player));
    }

    public static ChatColor getLivesColor(int lives) {
        if (lives >= 4) {
            return ChatColor.GREEN;
        } else if (lives == 3) {
            return ChatColor.YELLOW;
        } else if (lives == 2) {
            return ChatColor.GOLD;
        }
        return ChatColor.RED;
    }

}
