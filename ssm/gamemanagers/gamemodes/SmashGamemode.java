package ssm.gamemanagers.gamemodes;

import ssm.gamemanagers.DisplayManager;
import ssm.gamemanagers.GameManager;
import ssm.gamemanagers.KitManager;
import ssm.gamemanagers.maps.MapFile;
import ssm.kits.*;
import ssm.utilities.Utils;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.*;

public abstract class SmashGamemode implements Listener {

    protected String name = "N/A";
    protected String[] description = new String[] {"N/A"};
    protected List<MapFile> allowed_maps = new ArrayList<MapFile>();
    protected List<Kit> allowed_kits = new ArrayList<Kit>();
    protected int players_to_start = 2;
    protected File all_gamemodes_folder = null;
    protected File gamemode_folder = null;
    protected File kit_podium_data = null;

    public SmashGamemode() { }

    public void updateAllowedMaps() {
        allowed_maps.clear();
        File maps_folder = new File("maps");
        if(!maps_folder.exists()) {
            if(!maps_folder.mkdir()) {
                Bukkit.broadcastMessage(ChatColor.RED + "Failed to make Main Maps Folder");
            }
        }
        File gamemode_maps_folder = new File("maps/" + name);
        if (!gamemode_maps_folder.exists()) {
            if(!gamemode_maps_folder.mkdir()) {
                Bukkit.broadcastMessage(ChatColor.RED + "Failed to make Maps Folder: " + name);
            }
        }
        for (File map : gamemode_maps_folder.listFiles()) {
            if (!map.isDirectory()) {
                continue;
            }
            allowed_maps.add(new MapFile(map));
        }
    }

    public void updateAllowedKits() {
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

    public void setPlayerLives(HashMap<Player, Integer> lives) {
        for(Player player : GameManager.getPlayers()) {
            if (GameManager.isSpectator(player)) {
                continue;
            }
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
        Location selected_point = null;
        for(Location respawn_point : closest_player_distance.keySet()) {
            if(closest_player_distance.get(respawn_point) >= maximum) {
                selected_point = respawn_point;
            }
        }
        if(selected_point == null || maximum >= 1000) {
            selected_point = selected_map.getRespawnPoints().get((int) (Math.random() * selected_map.getRespawnPoints().size()));
        }
        // Face towards map center
        Vector difference = selected_map.copy_world.getSpawnLocation().toVector().subtract(selected_point.toVector());
        if(Math.abs(difference.getX()) > Math.abs(difference.getZ())) {
            selected_point.setDirection(new Vector(difference.getX(), 0, 0));
        }
        else {
            selected_point.setDirection(new Vector(0, 0, difference.getZ()));
        }
        return selected_point;
    }

    public List<String> getLivesScoreboard() {
        // This is less terrible but still forgive my laziness
        List<Player> least_to_greatest = new ArrayList<Player>();
        HashMap<Player, Integer> lives_copy = new HashMap<Player, Integer>();
        lives_copy.putAll(GameManager.getAllLives());
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
            scoreboard_string.add(GameManager.getLives(add) + " " + DisplayManager.getPlayerColor(add, true) + add.getName());
        }
        return scoreboard_string;
    }

    public boolean isGameEnded(HashMap<Player, Integer> lives) {
        return (lives.size() <= 1);
    }

    public boolean isCurrentGamemode() {
        return (GameManager.getCurrentGamemode().equals(this));
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

    public List<Kit> getAllowedKits() {
        return allowed_kits;
    }

    public int getPlayersToStart() {
        return players_to_start;
    }

    public void checkPodiumClick(Player player, Entity clicked) {
        if(clicked == null) {
            return;
        }
        String clicked_name = Utils.getAttachedCustomName(clicked);
        if(clicked_name == null) {
            return;
        }
        for(Kit kit : allowed_kits) {
            if(clicked_name.equals(ChatColor.GREEN + kit.getName())) {
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
                KitManager.equipPlayer(player, kit);
                GameManager.setDefaultKit(player, kit);
                return;
            }
        }
    }

    public String getFirstPlaceString() {
        if(GameManager.getAllLives().keySet().size() > 0) {
            return GameManager.getAllLives().keySet().toArray(new Player[1])[0].getName();
        }
        return null;
    }

    public String getSecondPlaceString() {
        if(GameManager.deaths[0] != null) {
            return GameManager.deaths[0].getName();
        }
        return null;
    }

    public String getThirdPlaceString() {
        if(GameManager.deaths[1] != null) {
            return GameManager.deaths[1].getName();
        }
        return null;
    }

    @EventHandler
    public void leftClickPodiumMob(PrePlayerAttackEntityEvent e) {
        checkPodiumClick(e.getPlayer(), e.getAttacked());
    }

    @EventHandler
    public void rightClickPodiumMob(PlayerInteractEntityEvent e)  {
        checkPodiumClick(e.getPlayer(), e.getRightClicked());
    }

}
