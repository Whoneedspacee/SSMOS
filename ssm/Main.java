package ssm;

import org.bukkit.scoreboard.DisplaySlot;
import ssm.attributes.Hunger;
import ssm.commands.*;
import ssm.commands.CommandStop;
import ssm.managers.*;
import ssm.managers.disguises.Disguise;
import ssm.managers.gamemodes.SoloGamemode;
import ssm.managers.gamemodes.TeamsGamemode;
import ssm.managers.gamemodes.TestingGamemode;
import ssm.managers.smashserver.SmashServer;
import ssm.kits.Kit;
import ssm.utilities.DamageUtil;
import ssm.utilities.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Main extends JavaPlugin implements Listener {

    private static JavaPlugin ourInstance;
    public static JavaPlugin getInstance() {
        return ourInstance;
    }
    public static boolean DEBUG_MODE = false;

    @Override
    public void onEnable() {
        ourInstance = this;
        getServer().getPluginManager().registerEvents(this, this);
        this.saveConfig();
        new CooldownManager();
        new EventManager();
        new KitManager();
        new DamageManager();
        new DisguiseManager();
        new GameManager();
        new BlockRestoreManager();
        new TeamManager();
        new MenuManager();
        this.getCommand("start").setExecutor(new CommandStart());
        this.getCommand("stop").setExecutor(new CommandStop());
        this.getCommand("tpworld").setExecutor(new CommandTeleportWorld());
        this.getCommand("getloadedworlds").setExecutor(new CommandGetLoadedWorlds());
        this.getCommand("loadworld").setExecutor(new CommandLoadWorld());
        this.getCommand("kit").setExecutor(new CommandKit());
        this.getCommand("damage").setExecutor(new CommandDamage());
        this.getCommand("setspeed").setExecutor(new CommandSetSpeed());
        this.getCommand("move").setExecutor(new CommandMove());
        this.getCommand("jump").setExecutor(new CommandJump());
        this.getCommand("vote").setExecutor(new CommandVote());
        this.getCommand("spectate").setExecutor(new CommandSpectate());
        this.getCommand("unloadworld").setExecutor(new CommandUnloadWorld());
        this.getCommand("setplaying").setExecutor(new CommandSetPlaying());
        this.getCommand("randomkit").setExecutor(new CommandRandomKit());
        this.getCommand("playerdisguise").setExecutor(new CommandPlayerDisguise());
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
        // Do not do anything before manager creation please
        for(Player player : Bukkit.getOnlinePlayers()) {
            Utils.fullHeal(player);
        }
        if(DEBUG_MODE) {
            SmashServer server = GameManager.createSmashServer(new TestingGamemode());
            for(Player player : Bukkit.getOnlinePlayers()) {
                server.teleportToServer(player);
            }
        } else {
            GameManager.createSmashServer(new SoloGamemode());
            GameManager.createSmashServer(new SoloGamemode());
            GameManager.createSmashServer(new TeamsGamemode());
            GameManager.createSmashServer(new TeamsGamemode());
            GameManager.createSmashServer(new TestingGamemode());
        }
    }

    @Override
    public void onDisable() {
        for(Disguise disguise : DisguiseManager.disguises.values()) {
            disguise.deleteLiving();
        }
        List<SmashServer> to_delete = List.copyOf(GameManager.servers);
        for(SmashServer server : to_delete) {
            GameManager.deleteSmashServer(server);
        }
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
            KitManager.unequipPlayer(player);
        }
        this.reloadConfig();
        this.saveConfig();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Block blockIn = e.getTo().getBlock();
        Block blockOn = e.getFrom().getBlock().getRelative(BlockFace.DOWN);
        if (blockIn.isLiquid() && DamageUtil.canDamage(player, null)) {
            boolean lighting = false;
            if(blockIn.getType() == Material.LAVA || blockIn.getType() == Material.STATIONARY_LAVA) {
                lighting = true;
            }
            DamageUtil.borderKill(player, lighting);
        }
    }

    @EventHandler
    public void onPlayerMessage(AsyncPlayerChatEvent e) {
        String message = e.getMessage();
        String playerName = e.getPlayer().getName();
        String newMessage = ChatColor.YELLOW + playerName + ChatColor.WHITE + " " + message;
        if(e.getPlayer().isOp()) {
            newMessage = ChatColor.RED + playerName + ChatColor.WHITE + " " + message;
        }
        e.setFormat(newMessage.replace("%", ""));
        e.setMessage(e.getMessage().replace("%", ""));
    }

    @EventHandler
    public void onWeatherChangeEvent(WeatherChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent e) {
        if(e.getPlayer().getGameMode() == GameMode.CREATIVE && e.getPlayer().isOp()) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if(e.getPlayer().getGameMode() == GameMode.CREATIVE && e.getPlayer().isOp()) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        String name = e.getPlayer().getName();
        e.setJoinMessage(ChatColor.DARK_GRAY + "Join> " + ChatColor.GRAY + name);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        String name = e.getPlayer().getName();
        e.setQuitMessage(ChatColor.DARK_GRAY + "Quit> " + ChatColor.GRAY + name);
    }

    @EventHandler
    public void stopHealthRegen(EntityRegainHealthEvent e) {
        if(e.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void stopHungerLoss(FoodLevelChangeEvent e) {
        if(!(e.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) e.getEntity();
        Kit kit = KitManager.getPlayerKit(player);
        // Only cancel if we don't have a hunger attribute
        if(kit != null && kit.isActive() && kit.getAttributeByClass(Hunger.class) != null) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void clickEvent(InventoryClickEvent e) {
        if(e.getWhoClicked().getGameMode() == GameMode.CREATIVE && e.getWhoClicked().isOp()) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onMobTarget(EntityTargetEvent e) {
        if(e.getEntity() == null || e.getTarget() == null) {
            return;
        }
        if(!(e.getTarget() instanceof Player)) {
            return;
        }
        Player player = (Player) e.getTarget();
        if(DamageUtil.canDamage(player, null)) {
            return;
        }
        e.setCancelled(true);
    }

}

