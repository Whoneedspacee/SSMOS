package xyz.whoneedspacee.ssmos.managers.smashteam;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.whoneedspacee.ssmos.managers.GameManager;
import xyz.whoneedspacee.ssmos.managers.smashserver.SmashServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class SmashTeam {

    private String team_name;
    private ChatColor team_color;
    private List<Player> players = new ArrayList<Player>();

    public SmashTeam(String team_name, ChatColor team_color) {
        this.team_name = team_name;
        this.team_color = team_color;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public void removeAllPlayers() {
        for(Iterator<Player> playerIterator = players.iterator(); playerIterator.hasNext(); ) {
            Player player = playerIterator.next();
            playerIterator.remove();
            removePlayer(player);
        }
    }

    public boolean isOnTeam(Player player) {
        return (players.contains(player));
    }

    public String getName() {
        return team_name;
    }

    public ChatColor getColor() {
        return team_color;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Player> getPlayersSortedByLives() {
        // This is less terrible but still forgive my laziness
        List<Player> least_to_greatest = new ArrayList<Player>();
        HashMap<Player, Integer> lives_copy = new HashMap<Player, Integer>();
        for(Player player : players) {
            SmashServer server = GameManager.getPlayerServer(player);
            if(server == null) {
                continue;
            }
            lives_copy.put(player, server.getLives(player));
        }
        while(!lives_copy.isEmpty()) {
            int min_value = 0;
            Player min_player = null;
            for (Player check : lives_copy.keySet()) {
                if(min_player == null) {
                    min_player = check;
                    min_value = lives_copy.get(check);
                    continue;
                }
                if(lives_copy.get(check) < min_value) {
                    min_player = check;
                    min_value = lives_copy.get(check);
                }
            }
            least_to_greatest.add(min_player);
            lives_copy.remove(min_player);
        }
        return least_to_greatest;
    }

    public int getTeamSize() {
        return players.size();
    }

    public String getPlayerNames() {
        return "";
    }

    public boolean hasAliveMembers() {
        for(Player player : players) {
            SmashServer server = GameManager.getPlayerServer(player);
            if(server == null) {
                continue;
            }
            if(!server.lives.containsKey(player)) {
                continue;
            }
            if(server.getLives(player) > 0) {
                return true;
            }
        }
        return false;
    }

}
