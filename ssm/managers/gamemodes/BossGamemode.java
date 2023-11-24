package ssm.managers.gamemodes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import ssm.events.SmashDamageEvent;
import ssm.kits.Kit;
import ssm.kits.boss.KitElderGuardian;
import ssm.managers.BossBarManager;
import ssm.managers.KitManager;
import ssm.managers.TeamManager;
import ssm.managers.gamestate.GameState;
import ssm.managers.smashscoreboard.SmashScoreboard;
import ssm.managers.smashteam.SmashTeam;
import ssm.utilities.Utils;

import java.util.*;

public class BossGamemode extends SmashGamemode {

    private Player selected_boss = null;
    private SmashTeam boss_team = TeamManager.createTeam("Boss", ChatColor.RED);
    private SmashTeam players_team = TeamManager.createTeam("Players", ChatColor.YELLOW);
    private HashMap<Player, Double> boss_damage = new HashMap<Player, Double>();
    private Player first_damage = null;
    private Player second_damage = null;
    private Player third_damage = null;

    public BossGamemode() {
        super();
        this.name = "SSM Boss Fight";
        this.short_name = "BOSS";
        this.description = new String[]{
                "Each player has 3 respawns",
                "Attack to restore hunger!",
                "Defeat the boss to win!"
        };
        this.players_to_start = 2;
        this.max_players = 10;
    }

    // Select one boss player and put all players on teams
    @Override
    public void setPlayerLives(HashMap<Player, Integer> lives) {
        boss_damage.clear();
        boss_team.removeAllPlayers();
        players_team.removeAllPlayers();
        int random_player = (int) (Math.random() * server.players.size());
        selected_boss = server.players.get(random_player);
        lives.put(selected_boss, 1);
        boss_team.addPlayer(selected_boss);
        for (Player player : server.players) {
            if (server.isSpectator(player)) {
                continue;
            }
            if (player.equals(selected_boss)) {
                continue;
            }
            lives.put(player, 4);
            players_team.addPlayer(player);
        }
    }

    // Set the kit of the boss player
    @Override
    public void setPlayerKit(Player player) {
        if (player.equals(selected_boss)) {
            KitManager.equipPlayer(player, new KitElderGuardian());
            Kit kit = KitManager.getPlayerKit(player);
            if(kit != null) {
                kit.setArmor(Utils.getArmorForExactHP((server.getActivePlayerCount() - 1) * 200));
            }
            return;
        }
        super.setPlayerKit(player);
    }

    @Override
    public Location getRandomRespawnPoint(Player player) {
        if (player.equals(selected_boss)) {
            return server.getGameMap().getWorld().getSpawnLocation();
        }
        return super.getRandomRespawnPoint(player);
    }

    // Display boss first and display health percent
    @Override
    public List<String> getLivesScoreboard() {
        List<String> scoreboard_string = new ArrayList<String>();
        for (Player player : players_team.getPlayersSortedByLives()) {
            scoreboard_string.add(server.getLives(player) + " " + SmashScoreboard.getPlayerColor(player, true) + player.getName());
        }
        scoreboard_string.add(ChatColor.STRIKETHROUGH + "--------------------");
        for (Player player : boss_team.getPlayersSortedByLives()) {
            double damage_multplier = 1;
            Kit kit = KitManager.getPlayerKit(player);
            if (kit != null) {
                damage_multplier = (1 - 0.08 * kit.getArmor());
            }
            scoreboard_string.add(String.format("%d", (int) Math.round(player.getHealth() / damage_multplier)) + ChatColor.RED +
                    " ❤ " + ChatColor.RESET + SmashScoreboard.getPlayerColor(player, true) + player.getName());
        }
        return scoreboard_string;
    }

    @Override
    public void update() {
        if (server.getState() == GameState.GAME_PLAYING) {
            String boss_name = "Boss Health";
            if (KitManager.getPlayerKit(selected_boss) != null) {
                boss_name = KitManager.getPlayerKit(selected_boss).getName();
            }
            for (Player player : server.players) {
                BossBarManager.updateBar(player, boss_name, 100);
                BossBarManager.teleportBar(player);
            }
        } else if (server.getState() == GameState.GAME_ENDING) {
            for (Player player : server.players) {
                BossBarManager.removeBar(player);
                boss_team.removeAllPlayers();
                players_team.removeAllPlayers();
            }
        }
        server.getScoreboard().buildScoreboard();
    }

    public boolean isGameEnded(HashMap<Player, Integer> lives) {
        return !boss_team.hasAliveMembers() || !players_team.hasAliveMembers();
    }

    @Override
    public String getFirstPlaceString() {
        Player max_damage = null;
        for (Player player : boss_damage.keySet()) {
            if (max_damage == null || boss_damage.get(player) > boss_damage.get(max_damage)) {
                max_damage = player;
            }
        }
        first_damage = max_damage;
        if(max_damage == null) {
            return null;
        }
        return max_damage.getName() + ChatColor.RED + " ❤ " + ChatColor.RESET + String.format("%d", boss_damage.get(max_damage).intValue());
    }

    @Override
    public String getSecondPlaceString() {
        Player max_damage = null;
        for (Player player : boss_damage.keySet()) {
            if(player.equals(first_damage)) {
                continue;
            }
            if (max_damage == null || boss_damage.get(player) > boss_damage.get(max_damage)) {
                max_damage = player;
            }
        }
        second_damage = max_damage;
        if(max_damage == null) {
            return null;
        }
        return max_damage.getName() + ChatColor.RED + " ❤ " + ChatColor.RESET + String.format("%d", boss_damage.get(max_damage).intValue());
    }

    @Override
    public String getThirdPlaceString() {
        Player max_damage = null;
        for (Player player : boss_damage.keySet()) {
            if(player.equals(first_damage)) {
                continue;
            }
            if(player.equals(second_damage)) {
                continue;
            }
            if (max_damage == null || boss_damage.get(player) > boss_damage.get(max_damage)) {
                max_damage = player;
            }
        }
        third_damage = max_damage;
        if(max_damage == null) {
            return null;
        }
        return max_damage.getName() + ChatColor.RED + " ❤ " + ChatColor.RESET + String.format("%d", boss_damage.get(max_damage).intValue());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void addBossDamage(SmashDamageEvent e) {
        if (e.isCancelled()) {
            return;
        }
        if (selected_boss == null || !selected_boss.equals(e.getDamagee())) {
            return;
        }
        if (!(e.getDamager() instanceof Player)) {
            return;
        }
        double real_damage = e.getDamage();
        if(e.getIgnoreArmor()) {
            double damage_multplier = 1;
            Kit kit = KitManager.getPlayerKit(selected_boss);
            if (kit != null) {
                damage_multplier = (1 - 0.08 * kit.getArmor());
            }
            real_damage = e.getDamage() / damage_multplier;
        }
        Player player = (Player) e.getDamager();
        boss_damage.putIfAbsent(player, 0.0);
        boss_damage.put(player, boss_damage.get(player) + real_damage);
    }

}
