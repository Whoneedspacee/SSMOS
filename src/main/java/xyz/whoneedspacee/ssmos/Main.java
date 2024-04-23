package xyz.whoneedspacee.ssmos;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.util.Vector;
import xyz.whoneedspacee.ssmos.attributes.Hunger;
import xyz.whoneedspacee.ssmos.attributes.doublejumps.DoubleJump;
import xyz.whoneedspacee.ssmos.commands.*;
import xyz.whoneedspacee.ssmos.kits.Kit;
import xyz.whoneedspacee.ssmos.managers.*;
import xyz.whoneedspacee.ssmos.managers.disguises.Disguise;
import xyz.whoneedspacee.ssmos.managers.gamemodes.*;
import xyz.whoneedspacee.ssmos.managers.smashserver.SmashServer;
import xyz.whoneedspacee.ssmos.utilities.DamageUtil;
import xyz.whoneedspacee.ssmos.utilities.Utils;
import xyz.whoneedspacee.ssmos.utilities.VelocityUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Main extends JavaPlugin implements Listener {

    private static JavaPlugin ourInstance;

    public static JavaPlugin getInstance() {
        return ourInstance;
    }

    public static boolean DEBUG_MODE = false;
    public static ItemStack SERVER_BROWSER_ITEM;
    public static ItemStack KIT_SELECTOR_ITEM;
    public static ItemStack TELEPORT_HUB_ITEM;
    public static ItemStack VOTING_MENU_ITEM;
    public static ItemStack SELECT_BOSS_ITEM;
    public static HashMap<Player, DoubleJump> hub_doublejump = new HashMap<Player, DoubleJump>();
    public static HashMap<Player, Long> last_boost_time = new HashMap<Player, Long>();

    @Override
    public void onEnable() {
        ourInstance = this;
        getServer().getPluginManager().registerEvents(this, this);
        this.saveConfig();
        SERVER_BROWSER_ITEM = new ItemStack(Material.COMPASS);
        ItemMeta server_meta = SERVER_BROWSER_ITEM.getItemMeta();
        server_meta.setDisplayName(ChatColor.GREEN + "" + "Quick Compass");
        SERVER_BROWSER_ITEM.setItemMeta(server_meta);
        KIT_SELECTOR_ITEM = new ItemStack(Material.COMPASS);
        ItemMeta kit_meta = KIT_SELECTOR_ITEM.getItemMeta();
        kit_meta.setDisplayName(ChatColor.GREEN + "" + "Choose a Kit");
        KIT_SELECTOR_ITEM.setItemMeta(kit_meta);
        TELEPORT_HUB_ITEM = new ItemStack(Material.WATCH);
        ItemMeta hub_meta = TELEPORT_HUB_ITEM.getItemMeta();
        hub_meta.setDisplayName(ChatColor.GREEN + "" + "Return to Hub");
        hub_meta.setLore(Arrays.asList(ChatColor.RESET + "Click while holding this", ChatColor.RESET + "to return to the Hub."));
        TELEPORT_HUB_ITEM.setItemMeta(hub_meta);
        VOTING_MENU_ITEM = new ItemStack(Material.BOOK);
        ItemMeta vote_meta = VOTING_MENU_ITEM.getItemMeta();
        vote_meta.setDisplayName(ChatColor.GREEN + "" + "Vote for the next Map");
        VOTING_MENU_ITEM.setItemMeta(vote_meta);
        SELECT_BOSS_ITEM = new ItemStack(Material.DRAGON_EGG);
        ItemMeta boss_meta = SELECT_BOSS_ITEM.getItemMeta();
        boss_meta.setDisplayName(ChatColor.GREEN + "" + "Choose a Default Boss");
        SELECT_BOSS_ITEM.setItemMeta(boss_meta);
        new CooldownManager();
        new EventManager();
        new KitManager();
        new DamageManager();
        new DisguiseManager();
        new GameManager();
        new BlockRestoreManager();
        new TeamManager();
        new MenuManager();
        new BossBarManager();
        this.getCommand("start").setExecutor(new CommandStart());
        this.getCommand("stop").setExecutor(new CommandStop());
        this.getCommand("kit").setExecutor(new CommandKit());
        this.getCommand("damage").setExecutor(new CommandDamage());
        this.getCommand("setspeed").setExecutor(new CommandSetSpeed());
        this.getCommand("move").setExecutor(new CommandMove());
        this.getCommand("jump").setExecutor(new CommandJump());
        this.getCommand("vote").setExecutor(new CommandVote());
        this.getCommand("spectate").setExecutor(new CommandSpectate());
        this.getCommand("setplaying").setExecutor(new CommandSetPlaying());
        this.getCommand("randomkit").setExecutor(new CommandRandomKit());
        this.getCommand("damagelog").setExecutor(new CommandDamageLog());
        CommandSetLives setlives = new CommandSetLives();
        this.getCommand("setlives").setExecutor(setlives);
        this.getCommand("setlives").setTabCompleter(setlives);
        CommandSetKit setKit = new CommandSetKit();
        this.getCommand("setkit").setExecutor(setKit);
        this.getCommand("setkit").setTabCompleter(setKit);
        this.getCommand("makeserver").setExecutor(new CommandMakeServer());
        this.getCommand("server").setExecutor(new CommandServer());
        this.getCommand("hub").setExecutor(new CommandHub());
        this.getCommand("world").setExecutor(new CommandWorld());
        this.getCommand("printentities").setExecutor(new CommandPrintEntities());
        this.getCommand("shout").setExecutor(new CommandShout());
        this.getCommand("message").setExecutor(new CommandMessage());
        this.getCommand("reply").setExecutor(new CommandReply());
        this.getCommand("showhitboxes").setExecutor(new CommandShowHitboxes());
        this.getCommand("showhealth").setExecutor(new CommandShowHealth());
        this.getCommand("setteamsplayer").setExecutor(new CommandSetTeamsPlayer());
        this.getCommand("ping").setExecutor(new CommandPing());
        for (Player player : Bukkit.getOnlinePlayers()) {
            equipPlayerHub(player);
        }
        if (DEBUG_MODE) {
            SmashServer server = GameManager.createSmashServer(new TestingGamemode());
            for (Player player : Bukkit.getOnlinePlayers()) {
                server.teleportToServer(player);
            }
        } else {
            GameManager.createSmashServer(new SoloGamemode());
            GameManager.createSmashServer(new SoloGamemode());
            GameManager.createSmashServer(new SoloGamemode());
            GameManager.createSmashServer(new SSMOSGamemode());
            GameManager.createSmashServer(new SSMOSGamemode());
            GameManager.createSmashServer(new TeamsGamemode());
            GameManager.createSmashServer(new SSMOSTeamsGamemode());
            GameManager.createSmashServer(new BossGamemode());
            GameManager.createSmashServer(new TestingGamemode());
        }
        // Do not do anything before manager creation please
    }

    @Override
    public void onDisable() {
        for (Disguise disguise : DisguiseManager.disguises.values()) {
            disguise.deleteLiving();
        }
        //List<SmashServer> to_delete = List.copyOf(GameManager.servers);       // gholemFix: replaced because List.copyOf doesn't exist in Java 8
        List<SmashServer> to_delete = new ArrayList<>(GameManager.servers);
        for (SmashServer server : to_delete) {
            GameManager.deleteSmashServer(server);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
            KitManager.unequipPlayer(player);
        }
        this.reloadConfig();
        this.saveConfig();
    }

    public void equipPlayerHub(Player player) {
        if (hub_doublejump.containsKey(player)) {
            Bukkit.broadcastMessage(ChatColor.RED + player.getName() + " equipped player hub twice");
            hub_doublejump.get(player).remove();
        }
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getInventory().setItem(0, SERVER_BROWSER_ITEM);
        Utils.fullHeal(player);
        DoubleJump double_jump = new DoubleJump(1, 1, Sound.GHAST_FIREBALL) {
            @Override
            public boolean groundCheck() {
                return Utils.entityIsDirectlyOnGround(owner);
            }

            @Override
            public void activate() {
                Vector vector = owner.getLocation().getDirection();
                vector.setY(Math.abs(vector.getY()));
                VelocityUtil.setVelocity(owner, vector, 1.4, false, 0, 0.2, 1, true);
            }
        };
        double_jump.setOwner(player);
        hub_doublejump.put(player, double_jump);
    }

    public void unequipPlayerHub(Player player) {
        DoubleJump double_jump = hub_doublejump.get(player);
        if (double_jump == null) {
            return;
        }
        hub_doublejump.remove(player);
        double_jump.remove();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e) {
        equipPlayerHub(e.getPlayer());
        e.setJoinMessage("");
        for(Player player : Bukkit.getWorlds().get(0).getPlayers()) {
            if(player.equals(e.getPlayer())) {
                continue;
            }
            player.sendMessage(ChatColor.DARK_GRAY + "Join> " + ChatColor.GRAY + e.getPlayer().getName());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent e) {
        if(e.getPlayer().getWorld().equals(Bukkit.getWorlds().get(0))) {
            for(Player player : Bukkit.getWorlds().get(0).getPlayers()) {
                if(player.equals(e.getPlayer())) {
                    continue;
                }
                player.sendMessage(ChatColor.DARK_GRAY + "Quit> " + ChatColor.GRAY + e.getPlayer().getName());
            }
        }
        e.getPlayer().teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        unequipPlayerHub(e.getPlayer());
        e.setQuitMessage("");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_AIR && e.getAction() != Action.LEFT_CLICK_BLOCK &&
                e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (e.getPlayer().getItemInHand().equals(SERVER_BROWSER_ITEM)) {
            GameManager.openServerMenu(e.getPlayer());
        }
        if (e.getPlayer().getItemInHand().equals(KIT_SELECTOR_ITEM)) {
            KitManager.openKitMenu(e.getPlayer());
        }
        if (e.getPlayer().getItemInHand().equals(TELEPORT_HUB_ITEM)) {
            e.getPlayer().teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        }
        if (e.getPlayer().getItemInHand().equals(VOTING_MENU_ITEM)) {
            SmashServer server = GameManager.getPlayerServer(e.getPlayer());
            if (server != null) {
                server.openVotingMenu(e.getPlayer());
            }
        }
        if (e.getPlayer().getItemInHand().equals(SELECT_BOSS_ITEM)) {
            SmashServer server = GameManager.getPlayerServer(e.getPlayer());
            if (server != null && server.getCurrentGamemode() instanceof BossGamemode) {
                BossGamemode gamemode = (BossGamemode) server.getCurrentGamemode();
                gamemode.openBossKitMenu(e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent e) {
        if (Bukkit.getWorlds().get(0).equals(e.getPlayer().getWorld())) {
            equipPlayerHub(e.getPlayer());
        } else {
            unequipPlayerHub(e.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Block blockIn = e.getTo().getBlock();
        Block blockFromAbove = e.getFrom().getBlock().getRelative(BlockFace.UP);
        Block blockToAbove = e.getTo().getBlock().getRelative(BlockFace.UP);
        if (player.getLocation().getWorld() == Bukkit.getWorlds().get(0)) {
            if (blockToAbove.getType() == Material.PORTAL && blockFromAbove.getType() != Material.PORTAL) {
                player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                Bukkit.getScheduler().runTaskLater(this, () -> GameManager.openServerMenu(player), 5L);
            }
            last_boost_time.putIfAbsent(player, 0L);
            if (blockIn.getType() == Material.GOLD_PLATE && System.currentTimeMillis() - last_boost_time.get(player) >= 500) {
                last_boost_time.put(player, System.currentTimeMillis());
                Location location = player.getLocation();
                Vector direction = location.getDirection().multiply(4);
                direction.setY(1.2);
                player.getWorld().playSound(location, Sound.CHICKEN_EGG_POP, 2, 0.5F);
                VelocityUtil.setVelocity(player, direction);
            }
            return;
        }
        if (blockIn.isLiquid() && DamageUtil.canDamage(player, null)) {
            boolean lighting = false;
            if (blockIn.getType() == Material.LAVA || blockIn.getType() == Material.STATIONARY_LAVA) {
                lighting = true;
            }
            DamageUtil.borderKill(player, lighting);
        }
    }

    @EventHandler
    public void onPlayerMessage(AsyncPlayerChatEvent e) {
        e.setCancelled(true);
        String message = e.getMessage();
        String playerName = e.getPlayer().getName();
        String newMessage = ChatColor.YELLOW + playerName + ChatColor.WHITE + " " + message;
        if (e.getPlayer().isOp()) {
            newMessage = ChatColor.RED + playerName + ChatColor.WHITE + " " + message;
        }
        for (Player player : e.getPlayer().getWorld().getPlayers()) {
            player.sendMessage(newMessage);
        }
        Bukkit.getConsoleSender().sendMessage(newMessage);
    }

    @EventHandler
    public void onWeatherChangeEvent(WeatherChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE && e.getPlayer().isOp()) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if (e.getPlayer().getGameMode() == GameMode.CREATIVE && e.getPlayer().isOp()) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void stopHealthRegen(EntityRegainHealthEvent e) {
        if (e.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void stopHungerLoss(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) e.getEntity();
        Kit kit = KitManager.getPlayerKit(player);
        // Only cancel if we don't have a hunger attribute
        if (kit != null && kit.isActive() && kit.getAttributeByClass(Hunger.class) != null) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void clickEvent(InventoryClickEvent e) {
        if (e.getWhoClicked().getGameMode() == GameMode.CREATIVE && e.getWhoClicked().isOp()) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onMobTarget(EntityTargetEvent e) {
        if (e.getEntity() == null || e.getTarget() == null) {
            return;
        }
        if (!(e.getTarget() instanceof Player)) {
            return;
        }
        Player player = (Player) e.getTarget();
        if (DamageUtil.canDamage(player, null)) {
            return;
        }
        e.setCancelled(true);
    }

    public void movementDistance(PlayerMoveEvent e) {
        if(!e.getPlayer().getName().equalsIgnoreCase("Cherripie34")) {
            return;
        }
        Vector difference = e.getTo().clone().subtract(e.getFrom()).toVector();
        String send = String.format("X: %.5f, Y: %.5f, Z: %.5f, G: %b", difference.getX(), difference.getY(), difference.getZ(), e.getPlayer().isOnGround());
        Bukkit.broadcastMessage(send);
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Material material = event.getItem().getType();
        if (material == Material.COOKIE) {
            player.setFoodLevel(20);
        }
    }

}

