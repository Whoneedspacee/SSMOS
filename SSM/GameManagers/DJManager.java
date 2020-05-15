package SSM.GameManagers;

import SSM.Kit;
import SSM.SSM;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class DJManager implements Listener {
    private static DJManager instance = new DJManager();
    private HashMap<UUID, Integer> doubleJumpCount = new HashMap<>();

    @EventHandler
    private void playerFlightEvent(PlayerToggleFlightEvent e) {
        Player player = e.getPlayer();
        Kit kit = SSM.playerKit.get(player.getUniqueId());
        Location location = player.getLocation();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        e.setCancelled(true);
        if (kit == null) {
            return;
        }

        Integer count = doubleJumpCount.get(player.getUniqueId());
        int jumpCount = count != null ? count : 0;

        if (jumpCount > 0) {
            doubleJumpCount.put(player.getUniqueId(), --jumpCount);

            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, 1f, 1f);

            double frictionModifier = 0;
            if (!player.getLocation().subtract(0, 0.001, 0).getBlock().isPassable()) {
                frictionModifier = -0.2;
            }

            if (!kit.hasDirectDoubleJump()) {
                player.setVelocity(player.getLocation().getDirection().multiply(kit.getDoubleJumpPower()).setY(kit.getDoubleJumpHeight() + frictionModifier));
            } else {
                player.setVelocity(player.getLocation().getDirection().multiply(kit.getDoubleJumpPower()).add(new Vector(0, kit.getDoubleJumpHeight(), 0)));
            }

            if (jumpCount <= 0) {
                player.setAllowFlight(false);
            }
        }
    }

    @EventHandler
    private void playerMoveEvent(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        World world = player.getWorld();
        Location location = player.getLocation();

        if (!world.getBlockAt(location.subtract(0, 0.5, 0)).isPassable()) {
            doubleJumpCount.put(player.getUniqueId(), 1);
            player.setAllowFlight(false);
        }
    }

    @EventHandler
    private void entityDamageEvent(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            e.setCancelled(false);
        }
    }

    @EventHandler
    private void playerQuitEvent(PlayerQuitEvent e) {
        doubleJumpCount.remove(e.getPlayer().getUniqueId());
    }


    public static DJManager getInstance() {
        return instance;
    }

}
