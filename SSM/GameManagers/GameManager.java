package SSM.GameManagers;

import SSM.Commands.CommandVote;
import SSM.GameManagers.Maps.MapFile;
import SSM.Kits.Kit;
import SSM.Kits.KitTemporarySpectator;
import SSM.SSM;
import SSM.Utilities.EffectUtil;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameManager implements Listener, Runnable {

    public class GameState {

        public static final short LOBBY_WAITING = 0;
        public static final short LOBBY_VOTING = 1;
        public static final short LOBBY_STARTING = 2;
        public static final short GAME_STARTING = 3;
        public static final short GAME_PLAYING = 4;
        public static final short GAME_ENDING = 5;

        public static String toString(short state) {
            switch (state) {
                case LOBBY_WAITING -> {
                    return "Lobby Waiting";
                }
                case LOBBY_VOTING -> {
                    return "Lobby Voting";
                }
                case LOBBY_STARTING -> {
                    return "Lobby Starting";
                }
                case GAME_STARTING -> {
                    return "Game Starting";
                }
                case GAME_PLAYING -> {
                    return "Game Playing";
                }
                case GAME_ENDING -> {
                    return "Game Ending";
                }
            }
            return "";
        }
    }

    public static GameManager ourInstance;
    private static JavaPlugin plugin = SSM.getInstance();
    private static List<Player> players = new ArrayList<Player>();
    private static List<Player> spectators = new ArrayList<Player>();
    private static HashMap<Player, Integer> lives = new HashMap<Player, Integer>();
    private static short state = GameState.LOBBY_WAITING;
    public static int time_left = -1;
    public static World lobby_world = Bukkit.getWorlds().get(0);
    public static MapFile chosen_map = null;
    public static List<MapFile> all_maps = new ArrayList<MapFile>();

    public GameManager() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        ourInstance = this;
        for (Player player : Bukkit.getOnlinePlayers()) {
            players.add(player);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(lobby_world.getSpawnLocation());
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, 20L);
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

            all_maps.add(new MapFile(map));
        }
    }

    public void run() {
        //Bukkit.broadcastMessage(GameState.toString(state) + " Time Left: " + time_left);
        DisplayManager.buildScoreboard();
        if (state == GameState.LOBBY_WAITING) {
            players = lobby_world.getPlayers();
            doLobbyWaiting();
            return;
        }
        if (state == GameState.LOBBY_VOTING) {
            players = lobby_world.getPlayers();
            doLobbyVoting();
            return;
        }
        if (state == GameState.LOBBY_STARTING) {
            players = lobby_world.getPlayers();
            doLobbyStarting();
            return;
        }
        if (state == GameState.GAME_STARTING) {
            doGameStarting();
            return;
        }
        if (state == GameState.GAME_PLAYING) {
            doGamePlaying();
            return;
        }
        if (state == GameState.GAME_ENDING) {
            doGameEnding();
            return;
        }
    }

    private void doLobbyWaiting() {
        if (getTotalPlayers() >= 2) {
            setTimeLeft(16);
            setState(GameState.LOBBY_VOTING);
            run();
            return;
        }
    }

    private void doLobbyVoting() {
        if (getTotalPlayers() <= 1) {
            setState(GameState.LOBBY_WAITING);
            run();
            return;
        }
        if (time_left <= 0) {
            chosen_map = getChosenMap();
            Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + chosen_map.getName() + ChatColor.WHITE + ChatColor.BOLD + " won the vote!");
            for (Player player : lobby_world.getPlayers()) {
                player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1, 1);
            }
            chosen_map.createWorld();
            setTimeLeft(16);
            setState(GameState.LOBBY_STARTING);
            run();
            return;
        }
        time_left--;
    }

    private void doLobbyStarting() {
        if (getTotalPlayers() <= 1) {
            setState(GameState.LOBBY_WAITING);
            run();
            return;
        }
        if (time_left <= 0) {
            if (chosen_map == null) {
                return;
            }
            for (Player player : players) {
                player.teleport(chosen_map.getRandomRespawnPoint());
                if (isSpectator(player)) {
                    continue;
                }
                lives.put(player, 4);
            }
            for (Player player : chosen_map.getCopyWorld().getPlayers()) {
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
            }
            // Wait until after teleporting to display disguises properly
            for (Player player : chosen_map.getCopyWorld().getPlayers()) {
                // Refresh so people who already had kits equipped get shown
                // Unfortunately packet mobs don't transfer when we teleport
                DisguiseManager.showDisguises(player);
                if (isSpectator(player)) {
                    KitManager.equipPlayer(player, new KitTemporarySpectator());
                    continue;
                }
                Kit kit = KitManager.getPlayerKit(player);
                if (kit == null) {
                    KitManager.equipPlayer(player, KitManager.getAllKits().get(0));
                }
            }
            setTimeLeft(11);
            setState(GameState.GAME_STARTING);
            run();
            return;
        }
        time_left--;
    }

    private void doGameStarting() {
        if (getTotalPlayers() <= 1) {
            setTimeLeft(0);
            setState(GameState.GAME_ENDING);
            run();
            return;
        }
        if (time_left <= 0) {
            setState(GameState.GAME_PLAYING);
            run();
            return;
        }
        time_left--;
    }

    private void doGamePlaying() {
        if (getTotalPlayers() <= 1) {
            setState(GameState.GAME_ENDING);
            run();
            return;
        }
        if (lives.size() <= 1) {
            Bukkit.broadcastMessage(lives.keySet().toArray(new Player[1])[0].getName() + " won the game!");
            lives.clear();
            setTimeLeft(11);
            setState(GameState.GAME_ENDING);
            run();
            return;
        }
    }

    private void doGameEnding() {
        if (time_left <= 0) {
            players.clear();
            lives.clear();
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.teleport(lobby_world.getSpawnLocation());
                players.add(player);
                KitManager.unequipPlayer(player);
            }
            chosen_map.deleteWorld();
            setState(GameState.LOBBY_WAITING);
            run();
            return;
        }
        time_left--;
    }

    public static void death(Player player) {
        if (lives.get(player) == null) {
            return;
        }
        // Don't do anything before setting to full hp again
        player.setHealth(player.getMaxHealth());
        // Blood Particles
        EffectUtil.createEffect(player.getEyeLocation(), 10, 0.5, Sound.HURT_FLESH,
                1f, 1f, Material.INK_SACK, (byte) 1, 10, true);
        if (lives.get(player) <= 1) {
            lives.remove(player);
            KitManager.equipPlayer(player, new KitTemporarySpectator());
            DisplayManager.buildScoreboard();
            return;
        }
        lives.put(player, lives.get(player) - 1);
        DisplayManager.buildScoreboard();
        Kit kit = KitManager.getPlayerKit(player);
        KitManager.equipPlayer(player, new KitTemporarySpectator());
        Bukkit.getScheduler().scheduleSyncDelayedTask(SSM.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (state == GameState.GAME_PLAYING && lives.get(player) > 0) {
                    player.teleport(chosen_map.getRandomRespawnPoint());
                    if (!kit.getName().equals("Temporary Spectator")) {
                        KitManager.equipPlayer(player, kit);
                    }
                    DisguiseManager.showDisguises(player);
                }
            }
        }, 100L);
    }

    // Not for temporary spectators, for people who want to spectate all games
    public static void toggleSpectator(Player player) {
        if (spectators.contains(player)) {
            spectators.remove(player);
            return;
        }
        spectators.add(player);
    }

    public static boolean isSpectator(Player player) {
        return spectators.contains(player);
    }

    public static void setState(short value) {
        state = value;
        DisplayManager.buildScoreboard();
    }

    public static int getState() {
        return state;
    }

    public static void setTimeLeft(int time) {
        time_left = time;
    }

    public static int getTimeLeft() {
        return time_left;
    }

    public static int getTotalPlayers() {
        return players.size() - spectators.size();
    }

    public static int getLives(Player player) {
        if (lives.get(player) == null) {
            return 0;
        }
        return lives.get(player);
    }

    public static List<Player> getPlayers() {
        return players;
    }

    public static HashMap<Player, Integer> getAllLives() {
        return lives;
    }

    public static MapFile getCurrentVotedMap(Player player) {
        for (MapFile mapfile : all_maps) {
            List<Player> current = mapfile.getVoted();
            if (current.contains(player)) {
                return mapfile;
            }
        }
        return null;
    }

    public static int getVotesFor(MapFile mapfile) {
        if (mapfile == null) {
            return 0;
        }
        return mapfile.getVoted().size();
    }

    public static MapFile getChosenMap() {
        if (all_maps.size() == 0) {
            Bukkit.broadcastMessage("SSM Maps Folder Empty, loading Default World");
            return new MapFile(Bukkit.getWorlds().get(0).getWorldFolder());
        }
        // Calculate max voted map
        int max = 0;
        for (MapFile mapfile : all_maps) {
            List<Player> votes = mapfile.getVoted();
            if (votes != null && votes.size() > max) {
                max = votes.size();
            }
        }
        if (max == 0) {
            return all_maps.get((int) (Math.random() * all_maps.size()));
        }
        // Get tied maps
        List<MapFile> tied = new ArrayList<MapFile>();
        for (MapFile mapfile : all_maps) {
            List<Player> votes = mapfile.getVoted();
            if (votes.size() >= max) {
                tied.add(mapfile);
            }
        }
        // Choose random from tied list
        return tied.get((int) (Math.random() * tied.size()));
    }

    @EventHandler
    public void PlayerJoin(PlayerJoinEvent e) {
        if (state >= GameState.GAME_STARTING) {
            e.getPlayer().teleport(chosen_map.getCopyWorld().getSpawnLocation());
            KitManager.equipPlayer(e.getPlayer(), new KitTemporarySpectator());
            return;
        }
        e.getPlayer().teleport(lobby_world.getSpawnLocation());
        players.add(e.getPlayer());
        DisplayManager.buildScoreboard();
    }

    @EventHandler
    public void PlayerQuit(PlayerQuitEvent e) {
        players.remove(e.getPlayer());
        lives.remove(e.getPlayer());
        spectators.remove(e.getPlayer());
        DisplayManager.buildScoreboard();
    }

    @EventHandler
    public void clickEvent(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        if (item == null) {
            return;
        }
        if (!e.getView().getTitle().contains("Vote")) {
            return;
        }
        if (GameManager.getState() > GameManager.GameState.LOBBY_VOTING) {
            player.closeInventory();
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null || meta != null && meta.getLore() == null) {
            return;
        }
        String meta_name = meta.getLore().get(0);
        for (MapFile mapfile : all_maps) {
            String name = mapfile.getName();
            if (name.equals(meta_name)) {
                List<Player> current = mapfile.getVoted();
                if (current == null) {
                    current = new ArrayList<Player>();
                }
                if (current.contains(player)) {
                    current.remove(player);
                    break;
                }
                for (MapFile remove : all_maps) {
                    remove.getVoted().remove(player);
                }
                current.add(player);
                break;
            }
        }
        CommandVote.openVotingMenu(player);
        e.setCancelled(true);
    }

}
