package SSM.Attributes.DoubleJumps;

import SSM.Attribute;
import SSM.SSM;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

public abstract class DoubleJump extends Attribute {
    protected double height;
    protected double power;
    private int maxDoubleJumps;
    private static int remainingDoubleJumps = 0;
    private Sound doubleJumpSound;

    protected abstract void jump(boolean perfectJumped);

    public DoubleJump(double power, double height, int maxDoubleJumps, Sound doubleJumpSound) {
        super();
        this.power = power;
        this.height = height;
        this.maxDoubleJumps = maxDoubleJumps;
        this.doubleJumpSound = doubleJumpSound;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Ensure the player is able to double jump and pass jump method.
     */
    @EventHandler
    public void playerFlightEvent(PlayerToggleFlightEvent e) {

        Player player = e.getPlayer();
        if (!player.equals(owner)) {
            return;
        }

        if (player.getGameMode() == GameMode.CREATIVE){
            return;
        }

        e.setCancelled(true);

        if (remainingDoubleJumps > 0) {
            remainingDoubleJumps--;

            player.getWorld().playSound(player.getLocation(), doubleJumpSound, 1f, 1f);

            //Jumping when directly on the ground seems to transfer Y velocity to horizontal velocity, or simply diminish Y. (Go very fast forward)
            boolean perfectJumped = false;
            if (!player.getLocation().subtract(0, 0.001, 0).getBlock().isPassable()) {
                perfectJumped = true;
            }

            jump(perfectJumped);

            if (remainingDoubleJumps <= 0) {
                player.setAllowFlight(false);
            }
        }
    }

    /**
     * Give player double jumps back when on the ground
     */
    @EventHandler
    public void playerMoveEvent(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        World world = player.getWorld();
        Location location = player.getLocation();

        if (!player.equals(owner)) {
            return;
        }

        if (!world.getBlockAt(location.subtract(0, 0.5, 0)).isPassable()) {
            remainingDoubleJumps = maxDoubleJumps;
            player.setAllowFlight(true);
        }
    }

    @EventHandler
    public void entityDamageEvent(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            e.setCancelled(true);
        }
    }

}
