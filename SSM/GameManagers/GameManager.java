package SSM.GameManagers;

import SSM.Commands.CommandVote;
import SSM.Events.GameStateChangeEvent;
import SSM.GameManagers.Maps.MapFile;
import SSM.Kits.Kit;
import SSM.Kits.KitTemporarySpectator;
import SSM.SSM;
import SSM.Utilities.EffectUtil;
import SSM.Utilities.ServerMessageType;
import SSM.Utilities.Utils;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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
    private static long time_remaining_ms = 0;
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
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, 0L);
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
        setState(GameState.LOBBY_WAITING);
    }

    public void run() {
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
            setTimeLeft(15);
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
        if (time_remaining_ms <= 0) {
            chosen_map = getChosenMap();
            Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + chosen_map.getName() + ChatColor.WHITE + ChatColor.BOLD + " won the vote!");
            for (Player player : lobby_world.getPlayers()) {
                player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1, 1);
            }
            chosen_map.createWorld();
            setTimeLeft(15);
            setState(GameState.LOBBY_STARTING);
            run();
            return;
        }
        time_remaining_ms -= 50;
    }

    private void doLobbyStarting() {
        if (getTotalPlayers() <= 1) {
            setState(GameState.LOBBY_WAITING);
            run();
            return;
        }
        if (time_remaining_ms <= 0) {
            if (chosen_map == null) {
                return;
            }
            for (Player player : players) {
                if (isSpectator(player)) {
                    player.teleport(chosen_map.getCopyWorld().getSpawnLocation());
                    continue;
                }
                Location starting_point = chosen_map.getRandomRespawnPoint(player);
                player.teleport(starting_point);
                lives.put(player, 4);
            }
            for (Player player : chosen_map.getCopyWorld().getPlayers()) {
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                player.sendMessage("");
                player.sendMessage("");
                player.sendMessage("");
                Utils.sendTitleMessage(player, ChatColor.YELLOW + "" + ChatColor.BOLD + "Super Smash Mobs",
                        "", 0, 45, 0);
                player.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.STRIKETHROUGH + "=============================================");
                player.sendMessage(ChatColor.GREEN + "Game - " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Super Smash Mobs");
                player.sendMessage("");
                player.sendMessage(ChatColor.WHITE + "  Each player has 3 respawns");
                player.sendMessage(ChatColor.WHITE + "  Attack to restore hunger!");
                player.sendMessage(ChatColor.WHITE + "  Last player alive wins!");
                player.sendMessage("");
                player.sendMessage(chosen_map.toString());
                player.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.STRIKETHROUGH + "=============================================");
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
            setTimeLeft(10);
            setState(GameState.GAME_STARTING);
            run();
            return;
        }
        for(Player player : lobby_world.getPlayers()) {
            if (time_remaining_ms % 1000 == 0 && time_remaining_ms <= 4000) {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1f);
            }
        }
        time_remaining_ms -= 50;
    }

    private void doGameStarting() {
        if (getTotalPlayers() <= 1) {
            setTimeLeft(0);
            setState(GameState.GAME_ENDING);
            run();
            return;
        }
        if (time_remaining_ms <= 0) {
            for(Player player : chosen_map.copy_world.getPlayers()) {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1f);
            }
            setState(GameState.GAME_PLAYING);
            run();
            return;
        }
        for(Player player : chosen_map.copy_world.getPlayers()) {
            int barLength = 24;
            int startRedBarInterval = barLength - (int) ((time_remaining_ms / (double) 10000) * barLength);
            StringBuilder sb = new StringBuilder("      §fGame Start ");
            for (int i = 0; i < barLength; i++) {
                if (i < startRedBarInterval) {
                    sb.append("§a▌");
                } else {
                    sb.append("§c▌");
                }
            }
            sb.append(" §f" + Utils.msToSeconds(time_remaining_ms) + " Seconds");
            Utils.sendActionBarMessage(sb.toString(), player);
            if(time_remaining_ms == 8000) {
                Utils.sendTitleMessage(player, ChatColor.YELLOW + "" + ChatColor.BOLD + "Super Smash Mobs",
                        "Each player has 3 respawns", 0, 45, 0);
            }
            if(time_remaining_ms == 6000) {
                Utils.sendTitleMessage(player, ChatColor.YELLOW + "" + ChatColor.BOLD + "Super Smash Mobs",
                        "Attack to restore hunger!", 0, 45, 0);
            }
            if(time_remaining_ms == 4000) {
                Utils.sendTitleMessage(player, ChatColor.YELLOW + "" + ChatColor.BOLD + "Super Smash Mobs",
                        "Last player alive wins!", 0, 45, 0);
            }
            if(time_remaining_ms % 1000 == 0) {
                player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1f, 1f);
            }
        }
        time_remaining_ms -= 50;
    }

    private void doGamePlaying() {
        if (getTotalPlayers() <= 1) {
            setState(GameState.GAME_ENDING);
            run();
            return;
        }
        if (lives.size() <= 1) {
            for(Player player : chosen_map.copy_world.getPlayers()) {
                player.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.STRIKETHROUGH + "=============================================");
                player.sendMessage(ChatColor.GREEN + "Game - " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Super Smash Mobs");
                player.sendMessage("");
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + " 1st Place " +
                        ChatColor.RESET + lives.keySet().toArray(new Player[1])[0].getName());
                player.sendMessage("");
                player.sendMessage(chosen_map.toString());
                player.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.STRIKETHROUGH + "=============================================");
            }
            lives.clear();
            setTimeLeft(10);
            setState(GameState.GAME_ENDING);
            run();
            return;
        }
    }

    private void doGameEnding() {
        if (time_remaining_ms <= 0) {
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
        time_remaining_ms -= 50;
    }

    public static void death(Player player, LivingEntity damager) {
        if (lives.get(player) == null) {
            return;
        }
        // Don't do anything before setting to full hp again
        player.setHealth(player.getMaxHealth());
        // Blood Particles
        EffectUtil.createEffect(player.getEyeLocation(), 10, 0.5, Sound.HURT_FLESH,
                1f, 1f, Material.INK_SACK, (byte) 1, 10, true);
        DamageManager.DamageRecord record = DamageManager.getLastDamageRecord(player);
        Bukkit.broadcastMessage(ServerMessageType.DEATH + " " + ChatColor.YELLOW + player.getName() +
                ChatColor.GRAY + " killed by " + ChatColor.YELLOW + record.getDamagerName() +
                ChatColor.GRAY + " with " + ChatColor.GREEN + record.getDamageReason() + ChatColor.GRAY + ".");
        DamageManager.deathReport(player);
        // Out of the game check
        if (lives.get(player) <= 1) {
            Utils.sendTitleMessage(player, ChatColor.RED + "You Died", "",
                    10, 50, 10);
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You ran out of lives!");
            player.playSound(player.getLocation(), Sound.EXPLODE, 2f, 1f);
            lives.remove(player);
            KitManager.equipPlayer(player, new KitTemporarySpectator());
            DisplayManager.buildScoreboard();
            return;
        }
        // Normal Life Lost
        lives.put(player, lives.get(player) - 1);
        Utils.sendTitleMessage(player, "", "Respawning in 4 seconds...", 10, 50, 10);
        player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You have died!");
        player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You have " + lives.get(player) +
                " " + (lives.get(player) == 1 ? "life" : "lives") + " left!");
        player.playSound(player.getLocation(), Sound.NOTE_BASS_GUITAR, 2f, 0.5f);
        DisplayManager.buildScoreboard();
        Kit kit = KitManager.getPlayerKit(player);
        KitManager.equipPlayer(player, new KitTemporarySpectator());
        // Respawn in 4 seconds
        Bukkit.getScheduler().scheduleSyncDelayedTask(SSM.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (state == GameState.GAME_PLAYING && lives.get(player) > 0) {
                    player.teleport(chosen_map.getRandomRespawnPoint(player));
                    Utils.sendTitleMessage(player, "", DisplayManager.getLivesColor(player) +
                                    "" + lives.get(player) + " " + (lives.get(player) == 1 ? "life" : "lives") + " left!",
                            10, 50, 10);
                    if (!kit.getName().equals("Temporary Spectator")) {
                        KitManager.equipPlayer(player, kit);
                    }
                    DisguiseManager.showDisguises(player);
                }
            }
        }, 80L);
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
        GameStateChangeEvent event = new GameStateChangeEvent(state, value);
        Bukkit.getPluginManager().callEvent(event);
        state = value;
        DisplayManager.buildScoreboard();
    }

    public static short getState() {
        return state;
    }

    public static boolean isStarting() {
        return isStarting(state);
    }

    public static boolean isStarting(short given_state) {
        return given_state == GameState.GAME_STARTING;
    }

    public static boolean isPlaying() {
        return isPlaying(state);
    }

    public static boolean isPlaying(short given_state) {
        return given_state == GameState.GAME_PLAYING;
    }

    public static void setTimeLeft(double time) {
        time_remaining_ms = (long) (time * 1000.0);
    }

    public static int getTimeLeft() {
        return (int) (time_remaining_ms / 1000.0);
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
    public void PlayerMove(PlayerMoveEvent e) {
        if(!isStarting()) {
            return;
        }
        Location to = e.getFrom();
        to.setPitch(e.getTo().getPitch());
        to.setYaw(e.getTo().getYaw());
        e.setTo(to);
    }

    @EventHandler
    public void PlayerJoin(PlayerJoinEvent e) {
        if (state >= GameState.GAME_STARTING) {
            e.getPlayer().teleport(chosen_map.getCopyWorld().getSpawnLocation());
            KitManager.equipPlayer(e.getPlayer(), new KitTemporarySpectator());
            return;
        }
        e.getPlayer().teleport(lobby_world.getSpawnLocation());
        KitManager.unequipPlayer(e.getPlayer());
        e.getPlayer().setHealth(e.getPlayer().getMaxHealth());
        e.getPlayer().setFoodLevel(20);
        e.getPlayer().setGameMode(GameMode.ADVENTURE);
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
        if (item == null) return;
        if (!e.getView().getTitle().contains("Map")) return;
        if (GameManager.getState() > GameManager.GameState.LOBBY_VOTING) {
            player.closeInventory();
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null || meta.getLore() == null) return;
        if (item.getType() == Material.BED) {
            player.closeInventory();
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
        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
        CommandVote.openVotingMenu(player);
        e.setCancelled(true);
    }
}
