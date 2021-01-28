package SSM;

import SSM.GameManagers.CooldownManager;
import SSM.GameManagers.EventManager;
import SSM.GameManagers.KitManager;
import SSM.GameManagers.MeleeManager;
import SSM.Utilities.DamageUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class SSM extends JavaPlugin implements Listener {

    private static JavaPlugin ourInstance;

    public static void main(String[] args) {
        // for testing junk
    }

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
        new MeleeManager();
        this.getCommand("kit").setExecutor(KitManager.getInstance());
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("damage")) {
            if (args.length == 1) {
                try {
                    int number = Integer.parseInt(args[0]);
                    DamageUtil.dealDamage(player, number);
                    player.sendMessage("You were dealt " + number + " damage");
                    return true;
                } catch (NumberFormatException e) {
                    player.sendMessage("You need to input a number!");
                    return true;
                }
            }
        }
        if (cmd.getName().equalsIgnoreCase("setspeed")) {
            if (args.length == 1) {
                try {
                    float number = Float.parseFloat(args[0]);
                    player.setWalkSpeed(number);
                    return true;
                } catch (NumberFormatException e) {
                    player.sendMessage("You need to input a number!");
                    return true;
                }
            }
        }
        return false;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Block blockIn = e.getTo().getBlock();
        Block blockOn = e.getFrom().getBlock().getRelative(BlockFace.DOWN);
        if (blockOn.getType() == Material.GOLD_BLOCK) {
            Vector direction = player.getLocation().getDirection().setY(0);
            direction = direction.normalize().multiply(1.2);
            direction.setY(1.2);
            player.setVelocity(direction);
        }
        if (blockOn.getType() == Material.IRON_BLOCK) {
            Location loc = player.getLocation();
            Vector dir = loc.getDirection();
            dir.normalize();
            dir.multiply(10);
            loc.add(dir);
            player.teleport(loc);
        }
        if (blockIn.isLiquid()) {
            player.setHealth(0.0);
        }
    }

    @EventHandler
    public void stopHealthRegen(EntityRegainHealthEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void stopHungerLoss(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.VOID) {
            LivingEntity ent = (LivingEntity) e.getEntity();
            ent.setHealth(0.0);
        }
    }
}

