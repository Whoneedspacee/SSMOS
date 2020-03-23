package SSM;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Ability implements Listener {

    public String name = "Base";
    protected double cooldownTime = 2.5;
    protected boolean leftClickActivate = false;
    protected boolean rightClickActivate = false;

    protected Plugin plugin;

    public Ability(Plugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void activateLeft(Player player) {
        if (leftClickActivate) {
            checkAndActivate(player);
        }
    }

    public void activateRight(Player player) {
        if (rightClickActivate) {
            checkAndActivate(player);
        }
    }

    public void checkAndActivate(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        Material itemType = item.getType();
        if (player.getCooldown(itemType) > 0) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta.getDisplayName().equalsIgnoreCase(name)){
            player.setCooldown(itemType, (int) (cooldownTime * 20));
            player.sendMessage("Spacee your fucking stupid watch this send once");
            useAbility(player);
        }
    }

    public void useAbility(Player player) {
        World world = player.getWorld();
        world.playSound(player.getLocation(), Sound.ENTITY_PHANTOM_SWOOP, 10, 1);
        player.setVelocity(new Vector(0, 10, 0));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            activateLeft(player);
        }
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            activateRight(player);
        }
    }

}
