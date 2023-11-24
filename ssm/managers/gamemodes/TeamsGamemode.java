package ssm.managers.gamemodes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import ssm.events.PlayerLostLifeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import ssm.managers.GameManager;
import ssm.managers.TeamManager;
import ssm.managers.TeamManager.TeamColor;
import ssm.managers.gamestate.GameState;
import ssm.managers.smashscoreboard.SmashScoreboard;
import ssm.managers.smashteam.SmashTeam;
import ssm.utilities.ServerMessageType;
import ssm.utilities.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeamsGamemode extends SmashGamemode {

    private List<SmashTeam> teams = new ArrayList<SmashTeam>();
    private SmashTeam[] team_deaths = new SmashTeam[2];
    private HashMap<Player, Player> preferred_teammate = new HashMap<Player, Player>();
    private int name_index = 0;
    protected TeamColor current_team_data = TeamColor.first();
    public int players_per_team = 2;

    public TeamsGamemode() {
        super();
        this.name = "Super Smash Mobs Teams";
        this.short_name = "SSM2";
        this.description = new String[] {
                "Each player has 3 respawns",
                "Attack to restore hunger!",
                "Last player alive wins!"
        };
        this.players_to_start = 3;
        this.max_players = 6;
    }

    // Even distribution of players across the teams
    @Override
    public void setPlayerLives(HashMap<Player, Integer> lives) {
        int team_amount = (int) Math.ceil((float) server.getActivePlayerCount() / players_per_team);
        // Create teams
        for(SmashTeam team : teams) {
            TeamManager.removeTeam(team);
        }
        teams.clear();
        current_team_data = TeamColor.first();
        for(int i = 0; i < team_amount; i++) {
            teams.add(TeamManager.createTeam(current_team_data.names[name_index], current_team_data.color));
            current_team_data = current_team_data.next();
            if(current_team_data == null) {
                current_team_data = TeamColor.first();
                name_index++;
            }
        }
        List<Player> already_added = new ArrayList<Player>();
        // Assign players to the same team as their preferred player if they both chose eachother
        for(Player player : preferred_teammate.keySet()) {
            // Player quit
            if(!server.players.contains(player)) {
                continue;
            }
            // Already parsed as the other player
            if(already_added.contains(player)) {
                continue;
            }
            Player our_choice = preferred_teammate.get(player);
            if(our_choice == null) {
                continue;
            }
            // Our choice quit
            if(!server.players.contains(our_choice)) {
                continue;
            }
            Player their_choice = preferred_teammate.get(our_choice);
            if(!player.equals(their_choice)) {
                continue;
            }
            // Find a team to put them both on
            int current_team_index = 0;
            SmashTeam team = teams.get(current_team_index);
            while(team.getTeamSize() >= players_per_team - 1 && current_team_index < teams.size()) {
                current_team_index = (current_team_index + 1) % teams.size();
                team = teams.get(current_team_index);
            }
            if(team.getTeamSize() >= players_per_team - 1) {
                Bukkit.broadcastMessage(ChatColor.RED + "Went over players per team.");
            }
            lives.put(player, 4);
            lives.put(our_choice, 4);
            team.addPlayer(player);
            team.addPlayer(our_choice);
            already_added.add(player);
            already_added.add(our_choice);

        }
        // Evenly distribute remaining players into teams
        for(Player player : server.players) {
            if (server.isSpectator(player)) {
                continue;
            }
            if(already_added.contains(player)) {
                continue;
            }
            lives.put(player, 4);
            int current_team_index = 0;
            SmashTeam team = teams.get(current_team_index);
            while(team.getTeamSize() >= players_per_team && current_team_index < teams.size()) {
                current_team_index = (current_team_index + 1) % teams.size();
                team = teams.get(current_team_index);
            }
            if(team.getTeamSize() >= players_per_team) {
                Bukkit.broadcastMessage(ChatColor.RED + "Went over players per team.");
            }
            team.addPlayer(player);
        }
        preferred_teammate.clear();
        server.getScoreboard().buildScoreboard();
    }

    public List<String> getLivesScoreboard() {
        List<String> scoreboard_string = new ArrayList<String>();
        for(SmashTeam team : teams) {
            for(Player add : team.getPlayersSortedByLives()) {
                scoreboard_string.add(server.getLives(add) + " " + SmashScoreboard.getPlayerColor(add, true) + add.getName());
            }
        }
        return scoreboard_string;
    }

    @Override
    public boolean isGameEnded(HashMap<Player, Integer> lives) {
        int teams_left = 0;
        for(SmashTeam team : teams) {
            if(team.hasAliveMembers()) {
                teams_left++;
            }
        }
        return (teams_left <= 1);
    }

    @Override
    public String getFirstPlaceString() {
        for(SmashTeam team : teams) {
            if(team.hasAliveMembers()) {
                return team.getColor() + team.getName();
            }
        }
        return null;
    }

    @Override
    public String getSecondPlaceString() {
        int found_alive = 0;
        for(SmashTeam team : teams) {
            if(team.hasAliveMembers()) {
                found_alive++;
            }
            if(found_alive == 2) {
                return team.getColor() + team.getName();
            }
        }
        if(team_deaths[0] != null) {
            return team_deaths[0].getColor() + team_deaths[0].getName();
        }
        return null;
    }

    @Override
    public String getThirdPlaceString() {
        int found_alive = 0;
        for(SmashTeam team : teams) {
            if(team.hasAliveMembers()) {
                found_alive++;
            }
            if(found_alive == 3) {
                return team.getColor() + team.getName();
            }
        }
        if(team_deaths[1] != null) {
            return team_deaths[1].getColor() + team_deaths[1].getName();
        }
        return null;
    }

    @EventHandler
    public void onPlayerLostLife(PlayerLostLifeEvent e) {
        if(server == null || !server.equals(GameManager.getPlayerServer(e.getPlayer()))) {
            return;
        }
        SmashTeam team = TeamManager.getPlayerTeam(e.getPlayer());
        if(team == null) {
            return;
        }
        if(!team.hasAliveMembers()) {
            team_deaths[1] = team_deaths[0];
            team_deaths[0] = team;
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent e) {
        if (server == null || !server.equals(GameManager.getPlayerServer(e.getPlayer()))) {
            return;
        }
        if(server.getState() >= GameState.GAME_STARTING) {
            return;
        }
        if (!(e.getRightClicked() instanceof Player)) {
            return;
        }
        Player player = e.getPlayer();
        Player clicked = (Player) e.getRightClicked();
        Player current_preferred = preferred_teammate.get(player);
        if (clicked.equals(current_preferred)) {
            Utils.sendServerMessageToPlayer("Removed " + ChatColor.YELLOW + clicked.getName() +
                    ChatColor.GRAY + " as your preferred teammate.", player, ServerMessageType.GAME);
            preferred_teammate.remove(player);
            return;
        }
        Utils.sendServerMessageToPlayer("Set " + ChatColor.YELLOW + clicked.getName() +
                ChatColor.GRAY + " as your preferred teammate.", player, ServerMessageType.GAME);
        preferred_teammate.put(player, clicked);
    }

}