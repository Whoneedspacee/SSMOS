package SSM.GameManagers.Gamemodes;

import SSM.Events.GameStateChangeEvent;
import SSM.GameManagers.GameManager;
import SSM.GameManagers.KitManager;
import SSM.GameManagers.Maps.MapFile;
import SSM.Kits.Kit;
import SSM.SSM;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class SmashGamemode implements Listener {

    protected String name = "N/A";
    protected String[] description = new String[] {"N/A"};
    protected List<MapFile> allowed_maps = new ArrayList<MapFile>();

    public SmashGamemode() {
        Bukkit.getPluginManager().registerEvents(this, SSM.getInstance());
        File file = new File("ssm_maps");
        if (!file.exists()) {
            file.mkdir();
        }
        for (File map : file.listFiles()) {
            if (!map.isDirectory()) {
                continue;
            }
            if (map.getName().contains("copy")) {
                World world = Bukkit.getWorld(map.getPath());
                Bukkit.unloadWorld(world, false);
                try {
                    FileUtils.deleteDirectory(map);
                } catch (Exception e) {
                    Bukkit.broadcastMessage("Failed to delete map: " + map.getName());
                }
                continue;
            }

            allowed_maps.add(new MapFile(map));
        }
    }

    public void setPlayerLives(HashMap<Player, Integer> lives) {
        for(Player player : GameManager.getPlayers()) {
            lives.put(player, 4);
        }
    }

    public void setPlayerKit(Player player) {
        Kit kit = KitManager.getPlayerKit(player);
        if(kit == null) {
            KitManager.equipPlayer(player, KitManager.getAllKits().get(0));
        }
    }

    public Location getRandomRespawnPoint(MapFile selected_map, Player player) {
        if (selected_map.getRespawnPoints().size() == 0) {
            return selected_map.copy_world.getSpawnLocation();
        }
        // Calculate closest player to each respawn point, pick the one furthest from players
        HashMap<Location, Double> closest_player_distance = new HashMap<>();
        double maximum = 0;
        for(Location respawn_point : selected_map.getRespawnPoints()) {
            double closest = 1000;
            for(Player check : GameManager.getPlayers()) {
                if(GameManager.isSpectator(check)) {
                    continue;
                }
                if(player.equals(check)) {
                    continue;
                }
                if(!check.getWorld().equals(respawn_point.getWorld())) {
                    continue;
                }
                closest = Math.min(closest, respawn_point.distance(check.getLocation()));
            }
            maximum = Math.max(maximum, closest);
            closest_player_distance.put(respawn_point, closest);
        }
        for(Location respawn_point : closest_player_distance.keySet()) {
            if(closest_player_distance.get(respawn_point) >= maximum) {
                return respawn_point;
            }
        }
        return selected_map.getRespawnPoints().get((int) (Math.random() * selected_map.getRespawnPoints().size()));
    }

    public boolean isCurrentGamemode() {
        return (GameManager.selected_gamemode.equals(this));
    }

    public boolean isGameEnded(HashMap<Player, Integer> lives) {
        return (lives.size() <= 1);
    }

    public String getName() {
        return name;
    }

    public String[] getDescription() {
        return description;
    }

    public List<MapFile> getAllowedMaps() {
        return allowed_maps;
    }

    @EventHandler
    public void onGameStateChanged(GameStateChangeEvent e) {

    }

}
