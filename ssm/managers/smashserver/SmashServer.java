package ssm.managers.smashserver;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.util.Vector;
import ssm.Main;
import ssm.attributes.Attribute;
import ssm.events.GameStateChangeEvent;
import ssm.events.PlayerLostLifeEvent;
import ssm.events.SmashDamageEvent;
import ssm.kits.Kit;
import ssm.kits.original.KitTemporarySpectator;
import ssm.managers.*;
import ssm.managers.gamemodes.SmashGamemode;
import ssm.managers.gamestate.GameState;
import ssm.managers.maps.GameMap;
import ssm.managers.maps.LobbyMap;
import ssm.managers.ownerevents.OwnerDeathEvent;
import ssm.managers.ownerevents.OwnerKillEvent;
import ssm.managers.smashmenu.SmashMenu;
import ssm.managers.smashscoreboard.SmashScoreboard;
import ssm.managers.smashteam.SmashTeam;
import ssm.utilities.DamageUtil;
import ssm.utilities.EffectUtil;
import ssm.utilities.ServerMessageType;
import ssm.utilities.Utils;

import java.io.File;
import java.util.*;

public class SmashServer implements Listener, Runnable {

    private static JavaPlugin plugin = Main.getInstance();
    private short state = GameState.LOBBY_WAITING;
    private long time_remaining_ms = 0;
    private long last_time_update_ms = 0;
    protected SmashScoreboard scoreboard;
    protected SmashGamemode current_gamemode;
    protected LobbyMap lobby_map;
    protected GameMap game_map = null;
    public List<Player> players = new ArrayList<Player>();
    public List<Player> toggled_spectator = new ArrayList<Player>();
    public HashMap<Player, Integer> lives = new HashMap<Player, Integer>();
    public Player[] deaths = new Player[2];
    public HashMap<Player, Kit> pre_selected_kits = new HashMap<Player, Kit>();

    public SmashServer() {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        scoreboard = new SmashScoreboard(this);
        File lobby_directory = new File("maps/lobby_world");
        lobby_map = new LobbyMap(lobby_directory);
        lobby_map.createWorld();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, 0L);
        setState(GameState.LOBBY_WAITING);
    }

    @Override
    public void run() {
        current_gamemode.update();
        if (time_remaining_ms % 1000 == 0 && time_remaining_ms != last_time_update_ms) {
            scoreboard.buildScoreboard();
            last_time_update_ms = time_remaining_ms;
        }
        if (state == GameState.LOBBY_WAITING) {
            doLobbyWaiting();
            return;
        }
        if (state == GameState.LOBBY_VOTING) {
            doLobbyVoting();
            return;
        }
        if (state == GameState.LOBBY_STARTING) {
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
        for(Player player : players) {
            Kit kit = KitManager.getPlayerKit(player);
            if(kit != null) {
                KitManager.unequipPlayer(player);
                setPlayerLobbyItems(player);
            }
        }
        if (getActivePlayerCount() >= current_gamemode.getPlayersToStart()) {
            setTimeLeft(15);
            setState(GameState.LOBBY_VOTING);
            return;
        }
    }

    private void doLobbyVoting() {
        if (getActivePlayerCount() < current_gamemode.getPlayersToStart()) {
            setState(GameState.LOBBY_WAITING);
            return;
        }
        if (time_remaining_ms <= 0) {
            if(game_map != null) {
                game_map.unloadWorld();
            }
            game_map = getChosenMap();
            for (Player player : players) {
                player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + game_map.getName() + ChatColor.WHITE + ChatColor.BOLD + " won the vote!");
                player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1, 1);
                if(isSpectator(player)) {
                    continue;
                }
                Kit equipped_kit = KitManager.getPlayerKit(player);
                Kit default_kit = GameManager.getDefaultKit(player);
                Kit pre_selected_kit = pre_selected_kits.get(player);
                if (pre_selected_kit != null) {
                    KitManager.equipPlayer(player, pre_selected_kit);
                } else if(equipped_kit == null && default_kit != null) {
                    KitManager.equipPlayer(player, default_kit);
                }
            }
            game_map.createWorld();
            setTimeLeft(15);
            setState(GameState.LOBBY_STARTING);
            return;
        }
        time_remaining_ms -= 50;
    }

    private void doLobbyStarting() {
        if (getActivePlayerCount() < current_gamemode.getPlayersToStart()) {
            setState(GameState.LOBBY_WAITING);
            return;
        }
        if (time_remaining_ms <= 0) {
            if (game_map == null) {
                return;
            }
            pre_selected_kits.clear();
            for (GameMap map : current_gamemode.getAllowedMaps()) {
                map.clearVoted();
            }
            current_gamemode.setPlayerLives(lives);
            for (Player player : players) {
                if (isSpectator(player)) {
                    player.teleport(game_map.getWorld().getSpawnLocation());
                    continue;
                }
                Location starting_point = current_gamemode.getRandomRespawnPoint(player);
                player.teleport(starting_point);
            }
            for (Player player : players) {
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
                for (int i = 0; i < 6 - current_gamemode.getDescription().length; i++) {
                    player.sendMessage("");
                }
                Utils.sendTitleMessage(player, ChatColor.YELLOW + "" + ChatColor.BOLD + current_gamemode.getName(),
                        "", 0, 45, 0);
                player.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.STRIKETHROUGH + "=============================================");
                player.sendMessage(ChatColor.GREEN + "Game - " + ChatColor.YELLOW + "" + ChatColor.BOLD + current_gamemode.getName());
                player.sendMessage("");
                for (String description : current_gamemode.getDescription()) {
                    player.sendMessage(ChatColor.WHITE + "  " + description);
                }
                player.sendMessage("");
                player.sendMessage(game_map.toString());
                player.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.STRIKETHROUGH + "=============================================");
            }
            // Wait until after teleporting to display disguises properly
            for (Player player : players) {
                // Refresh so people who already had kits equipped get shown
                // Unfortunately packet mobs don't transfer when we teleport
                DisguiseManager.showDisguises(player);
                if (isSpectator(player)) {
                    KitManager.equipPlayer(player, new KitTemporarySpectator());
                    continue;
                }
                current_gamemode.setPlayerKit(player);
            }
            setTimeLeft(10);
            setState(GameState.GAME_STARTING);
            return;
        }
        for (Player player : players) {
            if (time_remaining_ms % 1000 == 0 && time_remaining_ms <= 4000) {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1f);
            }
        }
        time_remaining_ms -= 50;
    }

    private void doGameStarting() {
        if (getActivePlayerCount() <= 0) {
            setTimeLeft(0);
            setState(GameState.GAME_ENDING);
            return;
        }
        if (time_remaining_ms <= 0) {
            for (Player player : players) {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1f);
            }
            setState(GameState.GAME_PLAYING);
            return;
        }
        for (Player player : players) {
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
            sb.append(" §f");
            sb.append(Utils.msToSeconds(time_remaining_ms));
            sb.append(" Seconds");
            Utils.sendActionBarMessage(sb.toString(), player);
            if (time_remaining_ms == 8000) {
                Utils.sendTitleMessage(player, ChatColor.YELLOW + "" + ChatColor.BOLD + current_gamemode.getName(),
                        current_gamemode.getDescription()[0], 0, 45, 0);
            }
            if (time_remaining_ms == 6000) {
                Utils.sendTitleMessage(player, ChatColor.YELLOW + "" + ChatColor.BOLD + current_gamemode.getName(),
                        current_gamemode.getDescription()[1], 0, 45, 0);
            }
            if (time_remaining_ms == 4000) {
                Utils.sendTitleMessage(player, ChatColor.YELLOW + "" + ChatColor.BOLD + current_gamemode.getName(),
                        current_gamemode.getDescription()[2], 0, 45, 0);
            }
            if (time_remaining_ms % 1000 == 0) {
                player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1f, 1f);
            }
        }
        time_remaining_ms -= 50;
    }

    private void doGamePlaying() {
        if (getActivePlayerCount() <= 0) {
            setTimeLeft(0);
            setState(GameState.GAME_ENDING);
            return;
        }
        if (current_gamemode.isGameEnded(lives)) {
            for (Player player : players) {
                player.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.STRIKETHROUGH + "=============================================");
                player.sendMessage(ChatColor.GREEN + "Game - " + ChatColor.YELLOW + "" + ChatColor.BOLD + current_gamemode.getName());
                player.sendMessage("");
                if (current_gamemode.getFirstPlaceString() != null) {
                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + " 1st Place " +
                            ChatColor.RESET + current_gamemode.getFirstPlaceString());
                }
                if (current_gamemode.getSecondPlaceString() != null) {
                    player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + " 2nd Place " +
                            ChatColor.RESET + current_gamemode.getSecondPlaceString());
                }
                if (current_gamemode.getThirdPlaceString() != null) {
                    player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + " 3rd Place " +
                            ChatColor.RESET + current_gamemode.getThirdPlaceString());
                }
                player.sendMessage("");
                player.sendMessage(game_map.toString());
                player.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.STRIKETHROUGH + "=============================================");
                Utils.fullHeal(player);
            }
            scoreboard.buildScoreboard();
            lives.clear();
            deaths = new Player[2];
            setTimeLeft(10);
            setState(GameState.GAME_ENDING);
            return;
        }
    }

    private void doGameEnding() {
        if (time_remaining_ms <= 0) {
            lives.clear();
            for (Player player : players) {
                // Unequip first so our lobby items get set properly
                KitManager.unequipPlayer(player);
                player.teleport(lobby_map.getWorld().getSpawnLocation());
                Utils.fullHeal(player);
            }
            game_map.unloadWorld();
            game_map = null;
            setState(GameState.LOBBY_WAITING);
            return;
        }
        if (time_remaining_ms % 100 == 0) {
            spawnFireworks();
        }
        time_remaining_ms -= 50;
    }

    public void death(Player player) {
        if (lives.get(player) == null) {
            return;
        }
        // Don't do anything before setting to full hp again
        Utils.fullHeal(player);
        // Blood Particles
        EffectUtil.createEffect(player.getEyeLocation(), 10, 0.5, Sound.HURT_FLESH,
                1f, 1f, Material.INK_SACK, (byte) 1, 10, true);
        SmashDamageEvent record = DamageManager.getLastDamageEvent(player);
        for(Player message : players) {
            message.sendMessage(ServerMessageType.DEATH + " " + ChatColor.YELLOW + player.getName() +
                    ChatColor.GRAY + " killed by " + ChatColor.YELLOW + record.getDamagerName() +
                    ChatColor.GRAY + " with " + record.getReasonColor() + record.getReason() + ChatColor.GRAY + ".");
        }
        DamageManager.deathReport(player, true);
        if (record.getDamager() != null && record.getDamager() instanceof Player) {
            Player damager = (Player) record.getDamager();
            Kit kit = KitManager.getPlayerKit(damager);
            if (kit != null) {
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
        if (kit != null) {
            List<Attribute> attributes = kit.getAttributes();
            for (Attribute attribute : attributes) {
                if (attribute instanceof OwnerDeathEvent) {
                    OwnerDeathEvent deathEvent = (OwnerDeathEvent) attribute;
                    deathEvent.onOwnerDeathEvent();
                }
            }
        }
        lives.put(player, lives.get(player) - 1);
        PlayerLostLifeEvent playerLostLifeEvent = new PlayerLostLifeEvent(player, lives.get(player));
        playerLostLifeEvent.callEvent();
        // Out of the game check
        if (lives.get(player) <= 0) {
            Utils.sendTitleMessage(player, ChatColor.RED + "You Died", "",
                    10, 50, 10);
            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You ran out of lives!");
            player.playSound(player.getLocation(), Sound.EXPLODE, 2f, 1f);
            lives.remove(player);
            deaths[1] = deaths[0];
            deaths[0] = player;
            KitManager.equipPlayer(player, new KitTemporarySpectator());
            scoreboard.buildScoreboard();
            return;
        }
        Utils.sendTitleMessage(player, "", "Respawning in 4 seconds...", 10, 50, 10);
        player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You have died!");
        player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You have " + lives.get(player) +
                " " + (lives.get(player) == 1 ? "life" : "lives") + " left!");
        player.playSound(player.getLocation(), Sound.NOTE_BASS_GUITAR, 2f, 0.5f);
        scoreboard.buildScoreboard();
        KitManager.equipPlayer(player, new KitTemporarySpectator());
        // Respawn in 4 seconds
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (state == GameState.GAME_PLAYING && lives.get(player) > 0) {
                    player.teleport(current_gamemode.getRandomRespawnPoint(player));
                    Utils.sendTitleMessage(player, "", SmashScoreboard.getLivesColor(player) +
                                    "" + lives.get(player) + " " + (lives.get(player) == 1 ? "life" : "lives") + " left!",
                            10, 50, 10);
                    if (kit != null && !kit.getName().equals("Temporary Spectator")) {
                        KitManager.equipPlayer(player, kit);
                    }
                    DisguiseManager.showDisguises(player);
                }
            }
        }, 80L);
    }

    public void stopGame() {
        if (state >= GameState.GAME_STARTING) {
            setTimeLeft(0);
            setState(GameState.GAME_ENDING);
        }
        else {
            setState(GameState.LOBBY_WAITING);
        }
    }

    // Not for temporary spectators, for people who want to spectate all games
    public void toggleSpectator(Player player) {
        if (toggled_spectator.remove(player)) {
            return;
        }
        toggled_spectator.add(player);
        scoreboard.buildScoreboard();
    }

    public boolean isSpectator(Player player) {
        return toggled_spectator.contains(player);
    }

    public void teleportToServer(Player player) {
        if(game_map == null || state < GameState.GAME_STARTING) {
            player.teleport(lobby_map.getWorld().getSpawnLocation());
            return;
        }
        player.teleport(game_map.getWorld().getSpawnLocation());
    }

    public void setCurrentGamemode(SmashGamemode gamemode) {
        if(current_gamemode != null) {
            current_gamemode.deleteMaps();
            current_gamemode.server = null;
            HandlerList.unregisterAll(current_gamemode);
        }
        current_gamemode = gamemode;
        current_gamemode.server = this;
        current_gamemode.updateAllowedKits();
        current_gamemode.updateAllowedMaps();
        Bukkit.getPluginManager().registerEvents(gamemode, Main.getInstance());
        // Clear existing entities
        for (Entity entity : lobby_map.getWorld().getEntities()) {
            if (!(entity instanceof Player)) {
                DamageManager.invincible_mobs.remove(entity);
                entity.remove();
            }
        }
        // Make kit podiums
        for (int i = 0; i < current_gamemode.getAllowedKits().size(); i++) {
            Kit kit = current_gamemode.getAllowedKits().get(i);
            Location podium_location = new Location(lobby_map.getWorld(), 0, 0, 0);
            Object data = ConfigManager.getConfigOption(ConfigManager.ConfigOption.PODIUM_LOCATION, String.valueOf(i + 1));
            if (data == null) {
                ConfigManager.setConfigOption(ConfigManager.ConfigOption.PODIUM_LOCATION, new Location(Bukkit.getWorlds().get(0), 0, 0, 0, 0, 0), String.valueOf(i + 1));
            }
            if (data instanceof Location) {
                Location temp = (Location) data;
                podium_location = new Location(Bukkit.getWorlds().get(0), temp.getX(), temp.getY(), temp.getZ(), temp.getYaw(), temp.getPitch());
            }
            podium_location.setWorld(lobby_map.getWorld());
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
        for (GameMap map : current_gamemode.getAllowedMaps()) {
            map.clearVoted();
        }
        scoreboard.buildScoreboard();
        for(Player player : players) {
            Utils.sendServerMessageToPlayer("Set " +
                    ChatColor.YELLOW + current_gamemode.getName() + ChatColor.GRAY +
                    " as the next gamemode.", player, ServerMessageType.GAME);
            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1, 1);
        }
        stopGame();
    }

    public void checkPodiumClick(Player player, Entity clicked) {
        if(clicked == null) {
            return;
        }
        String clicked_name = Utils.getAttachedCustomName(clicked);
        if(clicked_name == null) {
            return;
        }
        for(Kit kit : current_gamemode.getAllowedKits()) {
            if(clicked_name.equals(ChatColor.GREEN + kit.getName())) {
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
                if(state == GameState.LOBBY_STARTING) {
                    KitManager.equipPlayer(player, kit);
                }
                if(state <= GameState.LOBBY_VOTING) {
                    pre_selected_kits.put(player, kit);
                    scoreboard.buildScoreboard();
                }
                GameManager.setDefaultKit(player, kit);
                return;
            }
        }
    }

    public SmashScoreboard getScoreboard() {
        return scoreboard;
    }

    public SmashGamemode getCurrentGamemode() {
        return current_gamemode;
    }

    public LobbyMap getLobbyMap() {
        return lobby_map;
    }

    public GameMap getGameMap() {
        return game_map;
    }

    public void setState(short value) {
        GameStateChangeEvent event = new GameStateChangeEvent(this, state, value);
        Bukkit.getPluginManager().callEvent(event);
        boolean changed = (state != value);
        state = value;
        if (changed) {
            run();
        }
        scoreboard.buildScoreboard();
    }

    public short getState() {
        return state;
    }

    public boolean isStarting() {
        return GameState.isStarting(state);
    }

    public boolean isPlaying() {
        return GameState.isPlaying(state);
    }

    public void setTimeLeft(double time) {
        time_remaining_ms = (long) (time * 1000.0);
    }

    public int getTimeLeft() {
        return (int) (time_remaining_ms / 1000.0);
    }

    public int getActivePlayerCount() {
        return players.size() - toggled_spectator.size();
    }

    public int getLives(Player player) {
        if (lives.get(player) == null) {
            return 0;
        }
        return lives.get(player);
    }

    public GameMap getChosenMap() {
        if (current_gamemode.getAllowedMaps().size() == 0) {
            Bukkit.broadcastMessage("SSM Maps Folder Empty, loading Default World");
            return new GameMap(new File("maps/lobby_world"));
        }
        // Calculate max voted map
        int max = 0;
        for (GameMap map : current_gamemode.getAllowedMaps()) {
            List<Player> votes = map.getVoted();
            if (votes != null && votes.size() > max) {
                max = votes.size();
            }
        }
        if (max == 0) {
            return current_gamemode.getAllowedMaps().get((int) (Math.random() * current_gamemode.getAllowedMaps().size()));
        }
        // Get tied maps
        List<GameMap> tied = new ArrayList<GameMap>();
        for (GameMap map : current_gamemode.getAllowedMaps()) {
            List<Player> votes = map.getVoted();
            if (votes.size() >= max) {
                tied.add(map);
            }
        }
        // Choose random from tied list
        return tied.get((int) (Math.random() * tied.size()));
    }

    private void spawnFireworks() {
        Location location = game_map.getWorld().getSpawnLocation();
        double x = location.getX() + Math.random() * 200 - 100;
        double y = location.getY();
        double z = location.getZ() + Math.random() * 200 - 100;
        Location random = new Location(game_map.getWorld(), x, y, z);

        Firework firework = random.getWorld().spawn(random, Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        boolean flicker = new Random().nextBoolean();
        boolean trail = new Random().nextBoolean();
        FireworkEffect.Type type = FireworkEffect.Type.values()[new Random().nextInt(FireworkEffect.Type.values().length)];
        Color[] colours = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.PURPLE, Color.AQUA}; // we don't want all colours......
        Color colour = colours[new Random().nextInt(colours.length)];
        Color fade = colours[new Random().nextInt(colours.length)];

        FireworkEffect fe = FireworkEffect.builder()
                .flicker(flicker)
                .trail(trail)
                .with(type)
                .withColor(colour)
                .withFade(fade)
                .build();

        fireworkMeta.addEffect(fe);
        fireworkMeta.setPower(1);
        firework.setFireworkMeta(fireworkMeta);
    }

    public void setPlayerLobbyItems(Player player) {
        player.getInventory().setItem(0, Main.KIT_SELECTOR_ITEM);
        player.getInventory().setItem(1, Main.VOTING_MENU_ITEM);
        player.getInventory().setItem(8, Main.TELEPORT_HUB_ITEM);
    }

    public void openVotingMenu(Player player) {
        if (!canVote()) {
            Utils.sendServerMessageToPlayer("You may not vote after the voting period has ended.",
                    player, ServerMessageType.GAME);
            return;
        }
        int size = current_gamemode.getAllowedMaps().size() / 7 + 2;
        Inventory selectMap = Bukkit.createInventory(player, 9 * size  , "Choose a Map");
        SmashMenu menu = MenuManager.createPlayerMenu(player, selectMap);
        List<GameMap> sortedMaps = new ArrayList<>(current_gamemode.getAllowedMaps());
        sortedMaps.sort(Comparator.comparing(GameMap::getName));
        int slot = 10;
        int count = 0;
        for (GameMap map : sortedMaps) {
            ItemStack item;
            if (map.getVoted().size() == 0) {
                item = new ItemStack(Material.PAPER);
            } else {
                item = new ItemStack(Material.EMPTY_MAP, map.getVoted().size());
            }
            if (map.getVoted().contains(player)) {
                item = new ItemStack(Material.MAP, map.getVoted().size());
            }
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(ChatColor.YELLOW + map.getName());
            item.setItemMeta(itemMeta);
            selectMap.setItem(slot, item);
            menu.setActionFromSlot(slot, (e) -> {
                if(e.getWhoClicked() instanceof Player) {
                    Player clicked = (Player) e.getWhoClicked();
                    clicked.playSound(clicked.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
                    for(GameMap remove : current_gamemode.getAllowedMaps()) {
                        remove.getVoted().remove(clicked);
                    }
                    map.getVoted().add(clicked);
                    clicked.closeInventory();
                }
            });
            slot++;
            count++;
            if (count % 7 == 0) {
                slot += 2;
            }
            ItemStack exit = new ItemStack(Material.BARRIER);
            ItemMeta exitMeta = exit.getItemMeta();
            exitMeta.setDisplayName(ChatColor.RED + "Exit");
            exit.setItemMeta(exitMeta);
            selectMap.setItem(4, exit);
            menu.setActionFromSlot(4, (e) -> {
                if(e.getWhoClicked() instanceof Player) {
                    Player clicked = (Player) e.getWhoClicked();
                    clicked.playSound(clicked.getLocation(), Sound.NOTE_BASS, 1.0f, 1.0f);
                    clicked.closeInventory();
                }
            });
        }
        player.openInventory(selectMap);
    }

    public boolean canVote() {
        return (state != GameState.LOBBY_STARTING);
    }

    @Override
    public String toString() {
        return current_gamemode.getShortName() + "-" + (GameManager.servers.indexOf(this) + 1);
    }

    @EventHandler
    public void PlayerMove(PlayerMoveEvent e) {
        if (!isStarting()) {
            if (game_map != null) {
                if (game_map.isOutOfBounds(e.getPlayer())) {
                    DamageUtil.borderKill(e.getPlayer(), true);
                }
            }
            return;
        }
        if (!lives.containsKey(e.getPlayer())) {
            return;
        }
        Location to = e.getTo();
        if (!to.toVector().equals(e.getFrom().toVector())) {
            to = e.getFrom();
        }
        to.setPitch(e.getTo().getPitch());
        to.setYaw(e.getTo().getYaw());
        e.setTo(to);
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent e) {
        players.remove(e.getPlayer());
        toggled_spectator.remove(e.getPlayer());
        lives.remove(e.getPlayer());
        pre_selected_kits.remove(e.getPlayer());
        e.getPlayer().getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        KitManager.unequipPlayer(e.getPlayer());
        // Remove any teams that may have been added
        SmashTeam team = TeamManager.getPlayerTeam(e.getPlayer());
        if(team != null) {
            team.removePlayer(e.getPlayer());
        }
        scoreboard.buildScoreboard();
        for (Player message : players) {
            message.sendMessage(ChatColor.DARK_GRAY + "Quit> " + ChatColor.GRAY + e.getPlayer().getName());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void playerLeftWorld(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        if(!players.contains(player)) {
            return;
        }
        if(lobby_map != null && player.getWorld().equals(lobby_map.getWorld())) {
            return;
        }
        if(game_map != null && player.getWorld().equals(game_map.getWorld())) {
            return;
        }
        players.remove(player);
        toggled_spectator.remove(player);
        lives.remove(player);
        pre_selected_kits.remove(player);
        KitManager.unequipPlayer(player);
        player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        // Remove any teams that may have been added
        SmashTeam team = TeamManager.getPlayerTeam(player);
        if(team != null) {
            team.removePlayer(player);
        }
        scoreboard.buildScoreboard();
        for (Player message : players) {
            message.sendMessage(ChatColor.DARK_GRAY + "Quit> " + ChatColor.GRAY + player.getName());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void playerJoinedWorld(PlayerChangedWorldEvent e) {
        Player player = e.getPlayer();
        if(players.contains(player)) {
            if(lobby_map != null && lobby_map.getWorld().equals(player.getWorld())) {
                setPlayerLobbyItems(player);
            }
            return;
        }
        if(lobby_map != null && lobby_map.getWorld().equals(player.getWorld())) {
            for (Player message : players) {
                message.sendMessage(ChatColor.DARK_GRAY + "Join> " + ChatColor.GRAY + player.getName());
            }
            players.add(player);
            setPlayerLobbyItems(player);
            scoreboard.buildScoreboard();
            return;
        }
        if(game_map != null && game_map.getWorld().equals(player.getWorld())) {
            for (Player message : players) {
                message.sendMessage(ChatColor.DARK_GRAY + "Join> " + ChatColor.GRAY + player.getName());
            }
            players.add(player);
            KitManager.equipPlayer(player, new KitTemporarySpectator());
            scoreboard.buildScoreboard();
            return;
        }
    }

    @EventHandler
    public void leftClickPodiumMob(EntityDamageByEntityEvent e) {
        if(!(e.getDamager() instanceof Player)) {
            return;
        }
        Player player = (Player) e.getDamager();
        if(lobby_map == null || !player.getWorld().equals(lobby_map.getWorld())) {
            return;
        }
        checkPodiumClick(player, e.getEntity());
    }

    @EventHandler
    public void clickPodiumMob(PlayerInteractEntityEvent e)  {
        if(lobby_map == null || !e.getPlayer().getWorld().equals(lobby_map.getWorld())) {
            return;
        }
        checkPodiumClick(e.getPlayer(), e.getRightClicked());
    }

}
