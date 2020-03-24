package SSM;

import SSM.GameManagers.CooldownManager;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
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
        String itemName = item.getItemMeta().getDisplayName();

        if (CooldownManager.getInstance().getRemainingTimeFor(itemName, player) <= 0) {
            if (itemName.equalsIgnoreCase(name)) {
                CooldownManager.getInstance().addCooldown(itemName, (long)(cooldownTime * 1000), player);
                useAbility(player);
            }
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
