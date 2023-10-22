package SSM;

import SSM.Commands.*;
import SSM.Commands.CommandStop;
import SSM.GameManagers.*;
import SSM.GameManagers.Disguises.Disguise;
import SSM.Utilities.DamageUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SSM extends JavaPlugin implements Listener {

    private static JavaPlugin ourInstance;

    public static JavaPlugin getInstance() {
        return ourInstance;
    }

    @Override
    public void onEnable() {
        ourInstance = this;
        getServer().getPluginManager().registerEvents(this, this);
        new CooldownManager();
        new EventManager();
        new KitManager();
        new DamageManager();
        new DisguiseManager();
        new GameManager();
        new DisplayManager();
        this.getCommand("start").setExecutor(new CommandStart());
        this.getCommand("stop").setExecutor(new CommandStop());
        this.getCommand("getstate").setExecutor(new CommandGetState());
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
        // Do not do anything before manager creation please
    }

    @Override
    public void onDisable() {
        for(Disguise disguise : DisguiseManager.disguises.values()) {
            disguise.deleteLiving();
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Block blockIn = e.getTo().getBlock();
        Block blockOn = e.getFrom().getBlock().getRelative(BlockFace.DOWN);
        if (blockIn.isLiquid() && DamageUtil.canDamage(player, 1000)) {
            DamageUtil.borderKill(player, false);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if(e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
            if(e.getEntity() instanceof Player) {
                Player player = (Player) e.getEntity();
                if(KitManager.getPlayerKit(player) != null) {
                    if(KitManager.getPlayerKit(player).isInvincible()) {
                        e.setCancelled(true);
                        return;
                    }
                }
            }
            DamageUtil.damage((LivingEntity) e.getEntity(), null, e.getDamage(), 0, true, EntityDamageEvent.DamageCause.FIRE);
            e.setDamage(0);
            e.setCancelled(false);
        }
        if(e.getCause() == EntityDamageEvent.DamageCause.DROWNING) {
            e.setCancelled(true);
        }
        if (e.getEntity() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            e.setCancelled(true);
            return;
        }
        if (e.getEntity() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.VOID ||
                e.getEntity() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.LAVA) {
            DamageUtil.borderKill((Player) e.getEntity(), true);
            e.setCancelled(true);
            return;
        }
        if(e.getEntity() instanceof LivingEntity && e.getCause() == EntityDamageEvent.DamageCause.VOID) {
            DamageUtil.damage((LivingEntity) e.getEntity(), null, 1000, 0, true, EntityDamageEvent.DamageCause.VOID);
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
        e.setFormat(newMessage);
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
        e.getPlayer().setFoodLevel(20);
        e.getPlayer().getInventory().clear();
        e.getPlayer().getInventory().setArmorContents(null);
        e.getPlayer().setGameMode(GameMode.ADVENTURE);
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
        e.setCancelled(true);
    }

}

