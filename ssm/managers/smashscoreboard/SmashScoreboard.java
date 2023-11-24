package ssm.managers.smashscoreboard;

import ssm.managers.GameManager;
import ssm.managers.KitManager;
import ssm.managers.TeamManager;
import ssm.managers.gamestate.GameState;
import ssm.managers.smashserver.SmashServer;
import ssm.managers.smashteam.SmashTeam;
import ssm.kits.Kit;
import ssm.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.List;

public class SmashScoreboard implements Listener {

    private SmashServer server;
    private HashMap<Player, Scoreboard> player_scoreboards = new HashMap<Player, Scoreboard>();

    public SmashScoreboard(SmashServer server) {
        this.server = server;
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    public void buildScoreboard() {
        for(Player player : server.players) {
            Scoreboard scoreboard = player_scoreboards.get(player);
            if(scoreboard == null) {
                scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                player_scoreboards.put(player, scoreboard);
            }
            player.setScoreboard(scoreboard);
            Objective obj = scoreboard.getObjective("menu");
            if (obj != null) {
                obj.unregister();
            }
            obj = scoreboard.registerNewObjective("menu", "dummy");
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            if (server.getState() == GameState.LOBBY_WAITING) {
                Kit pre_selected_kit = server.pre_selected_kits.get(player);
                Kit equipped_kit = KitManager.getPlayerKit(player);
                String kit_name = "None";
                if(pre_selected_kit != null) {
                    kit_name = pre_selected_kit.getName();
                }
                if (equipped_kit != null) {
                    kit_name = equipped_kit.getName();
                }
                obj.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Waiting for players...");
                obj.getScore("").setScore(12);
                obj.getScore(ChatColor.YELLOW + "" + ChatColor.BOLD + "Players").setScore(11);
                obj.getScore(server.getActivePlayerCount() + "/" + server.getCurrentGamemode().max_players).setScore(10);
                obj.getScore(ChatColor.RED + "").setScore(9);
                obj.getScore(ChatColor.RED + "" + ChatColor.BOLD + "Game").setScore(8);
                obj.getScore(server.getCurrentGamemode().getName()).setScore(7);
                obj.getScore(ChatColor.GREEN + "").setScore(6);
                obj.getScore(ChatColor.GOLD + "" + ChatColor.BOLD + "Kit").setScore(5);
                obj.getScore(kit_name).setScore(4);
                obj.getScore(ChatColor.YELLOW + "").setScore(3);
                obj.getScore(ChatColor.AQUA + "" + ChatColor.BOLD + "Server").setScore(2);
                obj.getScore(server.toString()).setScore(1);
            }
            if (server.getState() == GameState.LOBBY_VOTING) {
                Kit pre_selected_kit = server.pre_selected_kits.get(player);
                Kit equipped_kit = KitManager.getPlayerKit(player);
                String kit_name = "None";
                if(pre_selected_kit != null) {
                    kit_name = pre_selected_kit.getName();
                }
                if (equipped_kit != null) {
                    kit_name = equipped_kit.getName();
                }
                obj.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Voting ends in " + server.getTimeLeft() + " seconds");
                obj.getScore("").setScore(12);
                obj.getScore(ChatColor.YELLOW + "" + ChatColor.BOLD + "Players").setScore(11);
                obj.getScore(server.getActivePlayerCount() + "/" + server.getCurrentGamemode().max_players).setScore(10);
                obj.getScore(ChatColor.RED + "").setScore(9);
                obj.getScore(ChatColor.RED + "" + ChatColor.BOLD + "Game").setScore(8);
                obj.getScore(server.getCurrentGamemode().getName()).setScore(7);
                obj.getScore(ChatColor.GREEN + "").setScore(6);
                obj.getScore(ChatColor.GOLD + "" + ChatColor.BOLD + "Kit").setScore(5);
                obj.getScore(kit_name).setScore(4);
                obj.getScore(ChatColor.YELLOW + "").setScore(3);
                obj.getScore(ChatColor.AQUA + "" + ChatColor.BOLD + "Server").setScore(2);
                obj.getScore(server.toString()).setScore(1);
            }
            if (server.getState() == GameState.LOBBY_STARTING) {
                Kit kit = KitManager.getPlayerKit(player);
                String kit_name = "None";
                if (kit != null) {
                    kit_name = kit.getName();
                }
                obj.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Starting in " + server.getTimeLeft() + " seconds");
                obj.getScore("").setScore(12);
                obj.getScore(ChatColor.YELLOW + "" + ChatColor.BOLD + "Players").setScore(11);
                obj.getScore(server.getActivePlayerCount() + "/" + server.getCurrentGamemode().max_players).setScore(10);
                obj.getScore(ChatColor.RED + "").setScore(9);
                obj.getScore(ChatColor.RED + "" + ChatColor.BOLD + "Game").setScore(8);
                obj.getScore(server.getCurrentGamemode().getName()).setScore(7);
                obj.getScore(ChatColor.GREEN + "").setScore(6);
                obj.getScore(ChatColor.GOLD + "" + ChatColor.BOLD + "Kit").setScore(5);
                obj.getScore(kit_name).setScore(4);
                obj.getScore(ChatColor.YELLOW + "").setScore(3);
                obj.getScore(ChatColor.AQUA + "" + ChatColor.BOLD + "Server").setScore(2);
                obj.getScore(server.toString()).setScore(1);
            }
            if (server.getState() == GameState.GAME_STARTING) {
                Kit kit = KitManager.getPlayerKit(player);
                String kit_name = "None";
                if (kit != null) {
                    kit_name = kit.getName();
                }
                obj.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "SSMOS");
                obj.getScore("").setScore(4);
                obj.getScore(ChatColor.YELLOW + "" + ChatColor.BOLD + "Players").setScore(3);
                obj.getScore(server.getActivePlayerCount() + " Players").setScore(2);
                obj.getScore(ChatColor.RED + "").setScore(1);
            }
            if (server.getState() == GameState.GAME_PLAYING) {
                Kit kit = KitManager.getPlayerKit(player);
                String kit_name = "None";
                if (kit != null) {
                    kit_name = kit.getName();
                }
                obj.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "SSMOS");
                int score = 1;
                obj.getScore("").setScore(score++);
                // This is less terrible but still forgive my laziness
                List<String> to_add = server.getCurrentGamemode().getLivesScoreboard();
                for(String add : to_add) {
                    obj.getScore(add).setScore(score++);
                }
                obj.getScore(ChatColor.RED + "").setScore(score++);
                obj.getScore(ChatColor.YELLOW + "" + ChatColor.BOLD + "Players").setScore(score++);
                obj.getScore(ChatColor.GREEN + "").setScore(score++);
            }
            if (server.getState() == GameState.GAME_ENDING) {
                return;
            }
        }
    }

    public static String getPlayerColor(Player player, boolean lives_display) {
        SmashServer server = GameManager.getPlayerServer(player);
        if(server == null) {
            return ChatColor.YELLOW + "";
        }
        SmashTeam team = TeamManager.getPlayerTeam(player);
        if(team != null) {
            if(!server.lives.containsKey(player) && lives_display) {
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
        SmashServer server = GameManager.getPlayerServer(player);
        if(server == null) {
            return getLivesColor(0);
        }
        return getLivesColor(server.getLives(player));
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
