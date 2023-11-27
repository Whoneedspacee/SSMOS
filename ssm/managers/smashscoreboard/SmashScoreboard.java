package ssm.managers.smashscoreboard;

import org.bukkit.event.EventHandler;
import org.bukkit.scoreboard.Team;
import ssm.events.GameStateChangeEvent;
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
import ssm.utilities.Utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class SmashScoreboard implements Listener {

    public SmashServer server;
    private HashMap<Player, Scoreboard> player_scoreboards = new HashMap<Player, Scoreboard>();
    private static final Random random = new Random();

    public SmashScoreboard() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    // This took many hours of testing as well as a very long time
    // Spent searching for the proper method to use and how I could
    // Make it as good as possible, touch at your own peril
    public void buildScoreboard() {
        if(server == null) {
            for(Player player : player_scoreboards.keySet()) {
                player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
            }
            player_scoreboards.clear();
            return;
        }
        for(Player player : server.players) {
            Scoreboard scoreboard = player_scoreboards.get(player);
            if(scoreboard == null) {
                scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                player_scoreboards.put(player, scoreboard);
            }
            if(!scoreboard.equals(player.getScoreboard())) {
                player.setScoreboard(scoreboard);
            }
            Objective obj = scoreboard.getObjective("menu");
            if (obj == null) {
                obj = scoreboard.registerNewObjective("menu", "dummy");
            }
            if(!DisplaySlot.SIDEBAR.equals(obj.getDisplaySlot())) {
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            }
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
                String display_name = ChatColor.GREEN + "" + ChatColor.BOLD + "Waiting for players...";
                obj.setDisplayName(display_name);
                setScore(scoreboard, "", 12);
                setScore(scoreboard, ChatColor.YELLOW + "" + ChatColor.BOLD + "Players", 11);
                setScore(scoreboard, server.getActivePlayerCount() + "/" + server.getCurrentGamemode().max_players, 10);
                setScore(scoreboard, "", 9);
                setScore(scoreboard, ChatColor.RED + "" + ChatColor.BOLD + "Game", 8);
                setScore(scoreboard, server.getCurrentGamemode().getName(), 7);
                setScore(scoreboard, "", 6);
                setScore(scoreboard, ChatColor.GOLD + "" + ChatColor.BOLD + "Kit", 5);
                setScore(scoreboard, kit_name, 4);
                setScore(scoreboard, "", 3);
                setScore(scoreboard, ChatColor.AQUA + "" + ChatColor.BOLD + "Server", 2);
                setScore(scoreboard, server.toString(), 1);
                clearScoresAbove(scoreboard, 12);
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
                setScore(scoreboard, "", 12);
                setScore(scoreboard, ChatColor.YELLOW + "" + ChatColor.BOLD + "Players", 11);
                setScore(scoreboard, server.getActivePlayerCount() + "/" + server.getCurrentGamemode().max_players, 10);
                setScore(scoreboard, "", 9);
                setScore(scoreboard, ChatColor.RED + "" + ChatColor.BOLD + "Game", 8);
                setScore(scoreboard, server.getCurrentGamemode().getName(), 7);
                setScore(scoreboard, "", 6);
                setScore(scoreboard, ChatColor.GOLD + "" + ChatColor.BOLD + "Kit", 5);
                setScore(scoreboard, kit_name, 4);
                setScore(scoreboard, "", 3);
                setScore(scoreboard, ChatColor.AQUA + "" + ChatColor.BOLD + "Server", 2);
                setScore(scoreboard, server.toString(), 1);
                clearScoresAbove(scoreboard, 12);
            }
            if (server.getState() == GameState.LOBBY_STARTING) {
                Kit kit = KitManager.getPlayerKit(player);
                String kit_name = "None";
                if (kit != null) {
                    kit_name = kit.getName();
                }
                obj.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Starting in " + server.getTimeLeft() + " seconds");
                setScore(scoreboard, "", 12);
                setScore(scoreboard, ChatColor.YELLOW + "" + ChatColor.BOLD + "Players", 11);
                setScore(scoreboard, server.getActivePlayerCount() + "/" + server.getCurrentGamemode().max_players, 10);
                setScore(scoreboard, "", 9);
                setScore(scoreboard, ChatColor.RED + "" + ChatColor.BOLD + "Game", 8);
                setScore(scoreboard, server.getCurrentGamemode().getName(), 7);
                setScore(scoreboard, "", 6);
                setScore(scoreboard, ChatColor.GOLD + "" + ChatColor.BOLD + "Kit", 5);
                setScore(scoreboard, kit_name, 4);
                setScore(scoreboard, "", 3);
                setScore(scoreboard, ChatColor.AQUA + "" + ChatColor.BOLD + "Server", 2);
                setScore(scoreboard, server.toString(), 1);
                clearScoresAbove(scoreboard, 12);
            }
            if (server.getState() == GameState.GAME_STARTING) {
                obj.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "SSMOS");
                setScore(scoreboard, "", 4);
                setScore(scoreboard, ChatColor.YELLOW + "" + ChatColor.BOLD + "Players", 3);
                setScore(scoreboard, server.getActivePlayerCount() + " Players", 2);
                setScore(scoreboard, "", 1);
                clearScoresAbove(scoreboard, 4);
            }
            if (server.getState() == GameState.GAME_PLAYING) {
                obj.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "SSMOS");
                int score = 1;
                setScore(scoreboard, "", score++);
                // This is less terrible but still forgive my laziness
                List<String> to_add = server.getCurrentGamemode().getLivesScoreboard();
                for(String add : to_add) {
                    setScore(scoreboard, add, score++);
                }
                setScore(scoreboard, "", score++);
                setScore(scoreboard, ChatColor.YELLOW + "" + ChatColor.BOLD + "Players", score++);
                setScore(scoreboard, "", score);
                clearScoresAbove(scoreboard, score);
            }
            if (server.getState() == GameState.GAME_ENDING) {
                return;
            }
        }
    }

    // Thanks to TheBlackTeddy's scoreboard tutorial
    // for at least showing me the basics with the prefix
    // https://www.spigotmc.org/threads/scoreboard-flicker.188058/
    // https://www.spigotmc.org/threads/the-received-string-length-is-longer-than-the-maximum-allowed-27-16.448993/
    public void setScore(Scoreboard scoreboard, String to_display, int score) {
        Objective obj = scoreboard.getObjective(DisplaySlot.SIDEBAR);
        if(obj == null) {
            return;
        }
        // Cut up into prefix and suffix since we have a 16 character limit
        String to_display_prefix = to_display;
        String to_display_suffix = "";
        if(to_display.length() >= 16) {
            to_display_prefix = to_display.substring(0, 16);
            String last_colors = ChatColor.getLastColors(to_display_prefix);
            to_display_suffix = last_colors + to_display.substring(16);
        }
        String team_name = getTeamName(score);
        Team team = scoreboard.getTeam(team_name);
        if(team == null) {
            team = scoreboard.registerNewTeam(team_name);
            team.addEntry(team_name);
            team.setPrefix(ChatColor.BLACK + "" + ChatColor.RED);
            team.setSuffix(ChatColor.BLACK + "" + ChatColor.BLUE);
            obj.getScore(team_name).setScore(score);
        }
        // Only change values if they are different than before
        if(!team.getPrefix().equals(to_display_prefix)) {
            team.setPrefix(to_display_prefix);
        }
        if(!team.getSuffix().equals(to_display_suffix)) {
            team.setSuffix(to_display_suffix);
        }
    }

    public void clearScore(Scoreboard scoreboard, int score) {
        Objective obj = scoreboard.getObjective(DisplaySlot.SIDEBAR);
        if(obj == null) {
            return;
        }
        String team_name = getTeamName(score);
        Team team = scoreboard.getTeam(team_name);
        if(team != null) {
            scoreboard.resetScores(team_name);
            scoreboard.getTeam(team_name).unregister();
        }
    }

    public void clearScoresAbove(Scoreboard scoreboard, int score) {
        for(int i = score + 1; i < 24; i++) {
            clearScore(scoreboard, i);
        }
    }

    public String getTeamName(int score) {
        // Basically a lot of values
        return (ChatColor.values()[(score / ChatColor.values().length) % ChatColor.values().length] + "" + ChatColor.values()[score % ChatColor.values().length]);
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
