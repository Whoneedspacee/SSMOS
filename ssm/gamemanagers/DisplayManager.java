package ssm.gamemanagers;

import ssm.gamemanagers.teams.SmashTeam;
import ssm.kits.Kit;
import ssm.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.List;

public class DisplayManager implements Listener {

    private static DisplayManager ourInstance;
    private static JavaPlugin plugin = Main.getInstance();
    private static HashMap<Player, Scoreboard> player_scoreboards = new HashMap<Player, Scoreboard>();

    public DisplayManager() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        ourInstance = this;
        buildScoreboard();
    }

    public static void buildScoreboard() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            Scoreboard scoreboard = player_scoreboards.get(player);
            if(scoreboard == null) {
                scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                player_scoreboards.put(player, scoreboard);
                player.setScoreboard(scoreboard);
            }
            Objective obj = scoreboard.getObjective("menu");
            if (obj != null) {
                obj.unregister();
            }
            obj = scoreboard.registerNewObjective("menu", "dummy");
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            if (GameManager.getState() == GameManager.GameState.LOBBY_WAITING) {
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
                obj.getScore(GameManager.getCurrentGamemode().getName()).setScore(7);
                obj.getScore(ChatColor.GREEN + "").setScore(6);
                obj.getScore(ChatColor.GOLD + "" + ChatColor.BOLD + "Kit").setScore(5);
                obj.getScore(kit_name).setScore(4);
                obj.getScore(ChatColor.YELLOW + "").setScore(3);
                obj.getScore(ChatColor.AQUA + "" + ChatColor.BOLD + "Server").setScore(2);
                obj.getScore("SSM-1").setScore(1);
            }
            if (GameManager.getState() == GameManager.GameState.LOBBY_VOTING) {
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
                obj.getScore(GameManager.getCurrentGamemode().getName()).setScore(7);
                obj.getScore(ChatColor.GREEN + "").setScore(6);
                obj.getScore(ChatColor.GOLD + "" + ChatColor.BOLD + "Kit").setScore(5);
                obj.getScore(kit_name).setScore(4);
                obj.getScore(ChatColor.YELLOW + "").setScore(3);
                obj.getScore(ChatColor.AQUA + "" + ChatColor.BOLD + "Server").setScore(2);
                obj.getScore("SSM-1").setScore(1);
            }
            if (GameManager.getState() == GameManager.GameState.LOBBY_STARTING) {
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
                obj.getScore(GameManager.getCurrentGamemode().getName()).setScore(7);
                obj.getScore(ChatColor.GREEN + "").setScore(6);
                obj.getScore(ChatColor.GOLD + "" + ChatColor.BOLD + "Kit").setScore(5);
                obj.getScore(kit_name).setScore(4);
                obj.getScore(ChatColor.YELLOW + "").setScore(3);
                obj.getScore(ChatColor.AQUA + "" + ChatColor.BOLD + "Server").setScore(2);
                obj.getScore("SSM-1").setScore(1);
            }
            if (GameManager.getState() == GameManager.GameState.GAME_STARTING) {
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
            }
            if (GameManager.getState() == GameManager.GameState.GAME_PLAYING) {
                Kit kit = KitManager.getPlayerKit(player);
                String kit_name = "None";
                if (kit != null) {
                    kit_name = kit.getName();
                }
                obj.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "SSMOS");
                int score = 1;
                obj.getScore("").setScore(score++);
                // This is less terrible but still forgive my laziness
                List<String> to_add = GameManager.getCurrentGamemode().getLivesScoreboard();
                for(String add : to_add) {
                    obj.getScore(add).setScore(score++);
                }
                obj.getScore(ChatColor.RED + "").setScore(score++);
                obj.getScore(ChatColor.YELLOW + "" + ChatColor.BOLD + "Players").setScore(score++);
                obj.getScore(ChatColor.GREEN + "").setScore(score++);
            }
            if (GameManager.getState() == GameManager.GameState.GAME_ENDING) {
                return;
            }
        }
    }

    public static String getPlayerColor(Player player, boolean lives_display) {
        SmashTeam team = TeamManager.getPlayerTeam(player);
        if(team != null) {
            if(!GameManager.getAllLives().containsKey(player) && lives_display) {
                return team.getColor() + "" + ChatColor.STRIKETHROUGH;
            }
            return team.getColor() + "";
        }
        if(!lives_display) {
            return ChatColor.YELLOW + "";
        }
        return getLivesColor(player);
    }

    public static String getLivesColor(Player player) {
        return getLivesColor(GameManager.getLives(player));
    }

    public static String getLivesColor(int lives) {
        if (lives > 4) {
            return ChatColor.AQUA + "";
        } else if (lives == 4) {
            return ChatColor.GREEN + "";
        } else if (lives == 3) {
            return ChatColor.YELLOW + "";
        } else if (lives == 2) {
            return ChatColor.GOLD + "";
        } else if (lives == 1) {
            return ChatColor.RED + "";
        } else {
            return ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH;
        }
    }

}
