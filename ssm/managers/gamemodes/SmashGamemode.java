package ssm.managers.gamemodes;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ssm.kits.original.*;
import ssm.managers.KitManager;
import ssm.managers.maps.GameMap;
import ssm.kits.*;
import org.bukkit.*;
import org.bukkit.event.Listener;
import ssm.managers.smashserver.SmashServer;

import java.io.File;
import java.util.*;

public abstract class SmashGamemode implements Listener {

    protected String name = "N/A";
    protected String short_name = "N/A";
    protected String maps_folder_name = "Super Smash Mobs";
    protected String[] description = new String[] {"N/A"};
    protected List<GameMap> allowed_maps = new ArrayList<GameMap>();
    protected List<Kit> allowed_kits = new ArrayList<Kit>();
    protected int players_to_start = 2;
    public int max_players = 4;
    public SmashServer server = null;

    public SmashGamemode() { }

    public void updateAllowedKits() {
        allowed_kits.clear();
        allowed_kits.add(new KitSkeleton());
        allowed_kits.add(new KitIronGolem());
        allowed_kits.add(new KitSpider());
        allowed_kits.add(new KitSlime());
        allowed_kits.add(new KitSquid());
        allowed_kits.add(new KitCreeper());
        allowed_kits.add(new KitEnderman());
        allowed_kits.add(new KitSnowMan());
        allowed_kits.add(new KitWolf());
        allowed_kits.add(new KitMagmaCube());
        allowed_kits.add(new KitWitch());
        allowed_kits.add(new KitWitherSkeleton());
        allowed_kits.add(new KitZombie());
        allowed_kits.add(new KitCow());
        allowed_kits.add(new KitSkeletonHorse());
        allowed_kits.add(new KitPig());
        allowed_kits.add(new KitBlaze());
        allowed_kits.add(new KitChicken());
        allowed_kits.add(new KitGuardian());
        allowed_kits.add(new KitSheep());
        allowed_kits.add(new KitVillager());
    }

    public void updateAllowedMaps() {
        try {
            allowed_maps.clear();
            File maps_folder = new File("maps");
            if (!maps_folder.exists()) {
                if (!maps_folder.mkdir()) {
                    Bukkit.broadcastMessage(ChatColor.RED + "Failed to make Main Maps Folder");
                }
            }
            File gamemode_maps_folder = new File("maps/" + maps_folder_name);
            if (!gamemode_maps_folder.exists()) {
                if (!gamemode_maps_folder.mkdir()) {
                    Bukkit.broadcastMessage(ChatColor.RED + "Failed to make Maps Folder: " + maps_folder_name);
                }
            }
            File[] files = gamemode_maps_folder.listFiles();
            if(files == null) {
                return;
            }
            for (File file : files) {
                if (!file.isDirectory()) {
                    continue;
                }
                File region_directory = new File(file.getPath() + "/region");
                if (!region_directory.exists()) {
                    continue;
                }
                allowed_maps.add(new GameMap(file));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteMaps() {
        for(GameMap map : allowed_maps) {
            map.deleteWorld();
        }
        allowed_maps.clear();
    }

    public void setPlayerLives(HashMap<Player, Integer> lives) {
        for(Player player : server.players) {
            if (server.isSpectator(player)) {
                continue;
            }
            lives.put(player, 4);
        }
    }

    public void setPlayerKit(Player player) {
        Kit kit = KitManager.getPlayerKit(player);
        if(kit == null) {
            KitManager.equipPlayer(player, allowed_kits.get(0));
        }
    }

    public Location getRandomRespawnPoint(Player player) {
        if (server.getGameMap().getRespawnPoints().size() == 0) {
            return server.getGameMap().getWorld().getSpawnLocation();
        }
        // Calculate closest player to each respawn point, pick the one furthest from players
        HashMap<Location, Double> closest_player_distance = new HashMap<>();
        double maximum = 0;
        for(Location respawn_point : server.getGameMap().getRespawnPoints()) {
            double closest = 1000;
            for(Player check : server.getGameMap().getWorld().getPlayers()) {
                if(player.equals(check)) {
                    continue;
                }
                if(!server.lives.containsKey(check)) {
                    continue;
                }
                closest = Math.min(closest, respawn_point.distance(check.getLocation()));
            }
            maximum = Math.max(maximum, closest);
            closest_player_distance.put(respawn_point, closest);
        }
        Location selected_point = null;
        for(Location respawn_point : closest_player_distance.keySet()) {
            if(closest_player_distance.get(respawn_point) >= maximum) {
                selected_point = respawn_point;
            }
        }
        if(selected_point == null || maximum >= 1000) {
            selected_point = server.getGameMap().getRespawnPoints().get((int) (Math.random() * server.getGameMap().getRespawnPoints().size()));
        }
        // Face towards map center
        org.bukkit.util.Vector difference = server.getGameMap().getWorld().getSpawnLocation().toVector().subtract(selected_point.toVector());
        if(Math.abs(difference.getX()) > Math.abs(difference.getZ())) {
            selected_point.setDirection(new org.bukkit.util.Vector(difference.getX(), 0, 0));
        }
        else {
            selected_point.setDirection(new Vector(0, 0, difference.getZ()));
        }
        return selected_point;
    }

    public List<String> getLivesScoreboard() {
        // This is less terrible but still forgive my laziness
        List<Player> least_to_greatest = new ArrayList<Player>();
        HashMap<Player, Integer> lives_copy = new HashMap<Player, Integer>(server.lives);
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
        List<String> scoreboard_string = new ArrayList<String>();
        for(Player add : least_to_greatest) {
            scoreboard_string.add(server.getLives(add) + " " + server.getScoreboard().getPlayerColor(add, true) + add.getName());
        }
        return scoreboard_string;
    }

    public void update() {
        return;
    }

    public boolean isGameEnded(HashMap<Player, Integer> lives) {
        return (lives.size() <= 1);
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return short_name;
    }

    public String[] getDescription() {
        return description;
    }

    public List<GameMap> getAllowedMaps() {
        return allowed_maps;
    }

    public List<Kit> getAllowedKits() {
        return allowed_kits;
    }

    public int getPlayersToStart() {
        return players_to_start;
    }

    public String getFirstPlaceString() {
        if(server.lives.keySet().size() > 0) {
            return server.lives.keySet().toArray(new Player[1])[0].getName();
        }
        return null;
    }

    public String getSecondPlaceString() {
        if(server.deaths[0] != null) {
            return server.deaths[0].getName();
        }
        return null;
    }

    public String getThirdPlaceString() {
        if(server.deaths[1] != null) {
            return server.deaths[1].getName();
        }
        return null;
    }

}
