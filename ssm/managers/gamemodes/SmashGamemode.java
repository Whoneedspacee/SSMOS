package ssm.managers.gamemodes;

import org.apache.logging.log4j.core.util.FileUtils;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Player;
import ssm.managers.KitManager;
import ssm.managers.maps.GameMap;
import ssm.kits.*;
import org.bukkit.*;
import org.bukkit.event.Listener;
import ssm.managers.smashscoreboard.SmashScoreboard;
import ssm.managers.smashserver.SmashServer;

import java.io.File;
import java.util.*;

public abstract class SmashGamemode implements Listener {

    protected String name = "N/A";
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

    public boolean isGameEnded(HashMap<Player, Integer> lives) {
        return (lives.size() <= 1);
    }

    public String getName() {
        return name;
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
