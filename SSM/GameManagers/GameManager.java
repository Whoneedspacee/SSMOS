package SSM.GameManagers;

import SSM.Attributes.Attribute;
import SSM.Commands.CommandVote;
import SSM.Events.GameStateChangeEvent;
import SSM.Events.SmashDamageEvent;
import SSM.GameManagers.Gamemodes.SmashGamemode;
import SSM.GameManagers.Gamemodes.SoloGamemode;
import SSM.GameManagers.Gamemodes.TeamsGamemode;
import SSM.GameManagers.Gamemodes.TestingGamemode;
import SSM.GameManagers.Maps.MapFile;
import SSM.GameManagers.OwnerEvents.OwnerDeathEvent;
import SSM.GameManagers.OwnerEvents.OwnerKillEvent;
import SSM.GameManagers.OwnerEvents.OwnerToggleSneakEvent;
import SSM.Kits.Kit;
import SSM.Kits.KitTemporarySpectator;
import SSM.SSM;
import SSM.Utilities.DamageUtil;
import SSM.Utilities.EffectUtil;
import SSM.Utilities.ServerMessageType;
import SSM.Utilities.Utils;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameManager implements Listener, Runnable {

    public static GameManager ourInstance;
    private static JavaPlugin plugin = SSM.getInstance();
    private static List<Player> players = new ArrayList<Player>();
    private static List<Player> spectators = new ArrayList<Player>();
    private static Player[] deaths = new Player[2];
    private static HashMap<Player, Integer> lives = new HashMap<Player, Integer>();
    private static short state = GameState.LOBBY_WAITING;
    private static long time_remaining_ms = 0;
    private static long last_time_update_ms = 0;
    private static World lobby_world = Bukkit.getWorlds().get(0);
    public static List<SmashGamemode> all_gamemodes = new ArrayList<SmashGamemode>();
    private static SmashGamemode selected_gamemode = null;
    private static MapFile selected_map = null;

    public GameManager() {
        all_gamemodes.add(new SoloGamemode());
        all_gamemodes.add(new TeamsGamemode());
        all_gamemodes.add(new TestingGamemode());
        for(SmashGamemode gamemode : all_gamemodes) {
            gamemode.updateAllowedMaps();
            gamemode.updateAllowedKits();
            gamemode.createDataFiles();
        }
        setGamemode(all_gamemodes.get(2));
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        ourInstance = this;
        players.addAll(Bukkit.getOnlinePlayers());
        for (Player player : players) {
            player.teleport(lobby_world.getSpawnLocation());
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, 0L);
        setState(GameState.LOBBY_WAITING);
    }

    public void run() {
        if(time_remaining_ms % 1000 == 0 && time_remaining_ms != last_time_update_ms) {
            DisplayManager.buildScoreboard();
            last_time_update_ms = time_remaining_ms;
        }
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
        if (getTotalPlayers() >= selected_gamemode.getPlayersToStart()) {
            setTimeLeft(15);
            setState(GameState.LOBBY_VOTING);
            return;
        }
    }

    private void doLobbyVoting() {
        if (getTotalPlayers() < selected_gamemode.getPlayersToStart()) {
            setState(GameState.LOBBY_WAITING);
            return;
        }
        if (time_remaining_ms <= 0) {
            selected_map = getChosenMap();
            Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + selected_map.getName() + ChatColor.WHITE + ChatColor.BOLD + " won the vote!");
            for (Player player : lobby_world.getPlayers()) {
                player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1, 1);
                Kit kit = getDefaultKit(player);
                if(kit != null) {
                    KitManager.equipPlayer(player, kit);
                }
            }
            selected_map.createWorld();
            setTimeLeft(15);
            setState(GameState.LOBBY_STARTING);
            return;
        }
        time_remaining_ms -= 50;
    }

    private void doLobbyStarting() {
        if (getTotalPlayers() < selected_gamemode.getPlayersToStart()) {
            setState(GameState.LOBBY_WAITING);
            return;
        }
        if (time_remaining_ms <= 0) {
            if (selected_map == null) {
                return;
            }
            for(MapFile mapFile : selected_gamemode.getAllowedMaps()) {
                mapFile.clearVoted();
            }
            for (Player player : players) {
                if (isSpectator(player)) {
                    player.teleport(selected_map.getCopyWorld().getSpawnLocation());
                    continue;
                }
                Location starting_point = selected_gamemode.getRandomRespawnPoint(selected_map, player);
                player.teleport(starting_point);
            }
            selected_gamemode.setPlayerLives(lives);
            for (Player player : selected_map.getCopyWorld().getPlayers()) {
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                for(int i = 0; i < 6 - selected_gamemode.getDescription().length; i++) {
                    player.sendMessage("");
                }
                Utils.sendTitleMessage(player, ChatColor.YELLOW + "" + ChatColor.BOLD + selected_gamemode.getName(),
                        "", 0, 45, 0);
                player.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.STRIKETHROUGH + "=============================================");
                player.sendMessage(ChatColor.GREEN + "Game - " + ChatColor.YELLOW + "" + ChatColor.BOLD + selected_gamemode.getName());
                player.sendMessage("");
                for(String description : selected_gamemode.getDescription()) {
                    player.sendMessage(ChatColor.WHITE + "  " + description);
                }
                player.sendMessage("");
                player.sendMessage(selected_map.toString());
                player.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.STRIKETHROUGH + "=============================================");
            }
            // Wait until after teleporting to display disguises properly
            for (Player player : selected_map.getCopyWorld().getPlayers()) {
                // Refresh so people who already had kits equipped get shown
                // Unfortunately packet mobs don't transfer when we teleport
                DisguiseManager.showDisguises(player);
                if (isSpectator(player)) {
                    KitManager.equipPlayer(player, new KitTemporarySpectator());
                    continue;
                }
                selected_gamemode.setPlayerKit(player);
            }
            setTimeLeft(10);
            setState(GameState.GAME_STARTING);
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
        if (getTotalPlayers() <= 0) {
            setTimeLeft(0);
            setState(GameState.GAME_ENDING);
            return;
        }
        if (time_remaining_ms <= 0) {
            for(Player player : selected_map.copy_world.getPlayers()) {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1f);
            }
            setState(GameState.GAME_PLAYING);
            return;
        }
        for(Player player : selected_map.copy_world.getPlayers()) {
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
                Utils.sendTitleMessage(player, ChatColor.YELLOW + "" + ChatColor.BOLD + selected_gamemode.getName(),
                        selected_gamemode.getDescription()[0], 0, 45, 0);
            }
            if(time_remaining_ms == 6000) {
                Utils.sendTitleMessage(player, ChatColor.YELLOW + "" + ChatColor.BOLD + selected_gamemode.getName(),
                        selected_gamemode.getDescription()[1], 0, 45, 0);
            }
            if(time_remaining_ms == 4000) {
                Utils.sendTitleMessage(player, ChatColor.YELLOW + "" + ChatColor.BOLD + selected_gamemode.getName(),
                        selected_gamemode.getDescription()[2], 0, 45, 0);
            }
            if(time_remaining_ms % 1000 == 0) {
                player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1f, 1f);
            }
        }
        time_remaining_ms -= 50;
    }

    private void doGamePlaying() {
        if (getTotalPlayers() <= 0) {
            setTimeLeft(0);
            setState(GameState.GAME_ENDING);
            return;
        }
        if (selected_gamemode.isGameEnded(lives)) {
            for(Player player : selected_map.copy_world.getPlayers()) {
                player.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.STRIKETHROUGH + "=============================================");
                player.sendMessage(ChatColor.GREEN + "Game - " + ChatColor.YELLOW + "" + ChatColor.BOLD + selected_gamemode.getName());
                player.sendMessage("");
                if(lives.keySet().size() >= 1) {
                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + " 1st Place " +
                            ChatColor.RESET + lives.keySet().toArray(new Player[1])[0].getName());
                }
                if(deaths[0] != null) {
                    player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + " 2nd Place " +
                            ChatColor.RESET + deaths[0].getName());
                }
                if(deaths[1] != null) {
                    player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + " 3rd Place " +
                            ChatColor.RESET + deaths[1].getName());
                }
                player.sendMessage("");
                player.sendMessage(selected_map.toString());
                player.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.STRIKETHROUGH + "=============================================");
                Utils.fullHeal(player);
            }
            lives.clear();
            deaths = new Player[2];
            setTimeLeft(10);
            setState(GameState.GAME_ENDING);
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
                Utils.fullHeal(player);
            }
            selected_map.deleteWorld();
            setState(GameState.LOBBY_WAITING);
            return;
        }
        time_remaining_ms -= 50;
    }

    public static void death(Player player) {
        if (lives.get(player) == null) {
            return;
        }
        // Don't do anything before setting to full hp again
        Utils.fullHeal(player);
        // Blood Particles
        EffectUtil.createEffect(player.getEyeLocation(), 10, 0.5, Sound.HURT_FLESH,
                1f, 1f, Material.INK_SACK, (byte) 1, 10, true);
        SmashDamageEvent record = DamageManager.getLastDamageEvent(player);
        Bukkit.broadcastMessage(ServerMessageType.DEATH + " " + ChatColor.YELLOW + player.getName() +
                ChatColor.GRAY + " killed by " + ChatColor.YELLOW + record.getDamagerName() +
                ChatColor.GRAY + " with " + record.getReasonColor() + record.getReason() + ChatColor.GRAY + ".");
        DamageManager.deathReport(player, true);
        if(record.getDamager() != null && record.getDamager() instanceof Player) {
            Player damager = (Player) record.getDamager();
            Kit kit = KitManager.getPlayerKit(damager);
            if(kit != null) {
                List<Attribute> attributes = kit.getAttributes();
                for (Attribute attribute : attributes) {
                    if (attribute instanceof OwnerKillEvent) {
                        OwnerKillEvent killEvent = (OwnerKillEvent) attribute;
                        killEvent.onOwnerKillEvent(player);
                    }
                }
            }
        }
        Kit kit = KitManager.getPlayerKit(player);
        if(kit != null) {
            List<Attribute> attributes = kit.getAttributes();
            for (Attribute attribute : attributes) {
                if (attribute instanceof OwnerDeathEvent) {
                    OwnerDeathEvent deathEvent = (OwnerDeathEvent) attribute;
                    deathEvent.onOwnerDeathEvent();
                }
            }
        }
        // Out of the game check
        if (lives.get(player) <= 1) {
            Utils.sendTitleMessage(player, ChatColor.RED + "You Died", "",
                    10, 50, 10);
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You ran out of lives!");
            player.playSound(player.getLocation(), Sound.EXPLODE, 2f, 1f);
            lives.remove(player);
            deaths[1] = deaths[0];
            deaths[0] = player;
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
        KitManager.equipPlayer(player, new KitTemporarySpectator());
        // Respawn in 4 seconds
        Bukkit.getScheduler().scheduleSyncDelayedTask(SSM.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (state == GameState.GAME_PLAYING && lives.get(player) > 0) {
                    player.teleport(selected_gamemode.getRandomRespawnPoint(selected_map, player));
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

    public static void clearLobbyWorldEntities() {
        for(Entity entity : lobby_world.getEntities()) {
            if(!(entity instanceof Player)) {
                DamageManager.invincible_mobs.remove(entity);
                entity.remove();
            }
        }
    }

    public static void setGamemode(SmashGamemode gamemode) {
        HandlerList.unregisterAll(selected_gamemode);
        selected_gamemode = gamemode;
        Bukkit.getPluginManager().registerEvents(gamemode, SSM.getInstance());
        // Clear current lobby world entities
        clearLobbyWorldEntities();
        // Make kit podiums
        for(int i = 1; i <= KitManager.getAllKits().size(); i++) {
            Kit kit = KitManager.getAllKits().get(i - 1);
            Location podium_location = new Location(lobby_world, 0, 0, 0);
            Object data = ConfigManager.getConfigOption(ConfigManager.ConfigOption.PODIUM_LOCATION, String.valueOf(i));
            if(data == null) {
                ConfigManager.setConfigOption(ConfigManager.ConfigOption.PODIUM_LOCATION, new Location(lobby_world, 0, 0, 0, 0, 0), String.valueOf(i));
            }
            if(data instanceof Location) {
                Location temp = (Location) data;
                podium_location = new Location(lobby_world, temp.getX(), temp.getY(), temp.getZ(), temp.getYaw(), temp.getPitch());
            }
            Entity podium_mob = kit.getNewPodiumMob(podium_location);
            Utils.attachCustomName(podium_mob, ChatColor.GREEN + kit.getName());
            DamageManager.invincible_mobs.put(podium_mob, 1);
            // Disable mob ai
            net.minecraft.server.v1_8_R3.Entity nms_entity = ((CraftEntity) podium_mob).getHandle();
            NBTTagCompound comp = new NBTTagCompound();
            nms_entity.c(comp);
            comp.setByte("NoAI", (byte) 1);
            nms_entity.f(comp);
            nms_entity.b(true);
        }
        // Clear all current map votes
        for(SmashGamemode check : all_gamemodes) {
            for(MapFile mapFile : check.getAllowedMaps()) {
                mapFile.clearVoted();
            }
        }
    }

    public static SmashGamemode getGamemode() {
        return selected_gamemode;
    }

    public static World getLobbyWorld() {
        return lobby_world;
    }

    public static void setState(short value) {
        GameStateChangeEvent event = new GameStateChangeEvent(state, value);
        Bukkit.getPluginManager().callEvent(event);
        boolean changed = (state != value);
        state = value;
        if(changed) {
            ourInstance.run();
        }
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
        for (MapFile mapfile : selected_gamemode.getAllowedMaps()) {
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
        if (selected_gamemode.getAllowedMaps().size() == 0) {
            Bukkit.broadcastMessage("SSM Maps Folder Empty, loading Default World");
            return new MapFile(Bukkit.getWorlds().get(0).getWorldFolder());
        }
        // Calculate max voted map
        int max = 0;
        for (MapFile mapfile : selected_gamemode.getAllowedMaps()) {
            List<Player> votes = mapfile.getVoted();
            if (votes != null && votes.size() > max) {
                max = votes.size();
            }
        }
        if (max == 0) {
            return selected_gamemode.getAllowedMaps().get((int) (Math.random() * selected_gamemode.getAllowedMaps().size()));
        }
        // Get tied maps
        List<MapFile> tied = new ArrayList<MapFile>();
        for (MapFile mapfile : selected_gamemode.getAllowedMaps()) {
            List<Player> votes = mapfile.getVoted();
            if (votes.size() >= max) {
                tied.add(mapfile);
            }
        }
        // Choose random from tied list
        return tied.get((int) (Math.random() * tied.size()));
    }

    public static MapFile getSelectedMap() {
        return selected_map;
    }

    public static void setDefaultKit(Player player, Kit kit) {
        Utils.sendServerMessageToPlayer("Set " + ChatColor.GREEN + kit.getName() +
                ChatColor.GRAY + " as your default kit.", player, ServerMessageType.GAME);
        ConfigManager.setPlayerGamemodeConfigOption(player, ConfigManager.ConfigOption.DEFAULT_KIT, kit.getName());
    }

    public static Kit getDefaultKit(Player player) {
        SmashGamemode gamemode = selected_gamemode;
        if(selected_gamemode instanceof TestingGamemode) {
            gamemode = all_gamemodes.get(0);
        }
        Object config_option = ConfigManager.getPlayerGamemodeConfigOption(player, ConfigManager.ConfigOption.DEFAULT_KIT, gamemode);
        if(config_option == null) {
            return null;
        }
        String default_kit_name = config_option.toString();
        for(Kit kit : KitManager.getAllKits()) {
            if(kit.getName().equals(default_kit_name)) {
                return kit;
            }
        }
        return null;
    }

    @EventHandler
    public void PlayerMove(PlayerMoveEvent e) {
        if(!isStarting()) {
            if(selected_map != null) {
                if(selected_map.isOutOfBounds(e.getPlayer())) {
                    DamageUtil.borderKill(e.getPlayer(), true);
                }
            }
            return;
        }
        if(!lives.containsKey(e.getPlayer())) {
            return;
        }
        Location to = e.getTo();
        if(!to.toVector().equals(e.getFrom().toVector())) {
            to = e.getFrom();
        }
        to.setPitch(e.getTo().getPitch());
        to.setYaw(e.getTo().getYaw());
        e.setTo(to);
    }

    @EventHandler
    public void PlayerJoin(PlayerJoinEvent e) {
        if (state >= GameState.GAME_STARTING) {
            e.getPlayer().teleport(selected_map.getCopyWorld().getSpawnLocation());
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
        for (MapFile map : selected_gamemode.getAllowedMaps()) {
            map.getVoted().remove(e.getPlayer());
        }
        DisplayManager.buildScoreboard();
    }

    @EventHandler
    public void cancelChickenEggSpawn(CreatureSpawnEvent e) {
        if(e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.EGG) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void clickEvent(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        if (item == null) return;
        if (!e.getView().getTitle().contains("Map")) return;
        if (!CommandVote.canVote()) {
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
        for (MapFile mapfile : selected_gamemode.getAllowedMaps()) {
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
                for (MapFile remove : selected_gamemode.getAllowedMaps()) {
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

}
