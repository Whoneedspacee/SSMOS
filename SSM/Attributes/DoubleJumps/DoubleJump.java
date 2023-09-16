package SSM.Attributes.DoubleJumps;

import SSM.Attribute;
import SSM.Utilities.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public abstract class DoubleJump extends Attribute {

    protected double height;
    protected double power;
    private int maxDoubleJumps;
    private static int remainingDoubleJumps = 0;
    private Sound doubleJumpSound;
    protected boolean perfectJumped;

    public DoubleJump(double power, double height, int maxDoubleJumps, Sound doubleJumpSound) {
        super();
        this.power = power;
        this.height = height;
        this.maxDoubleJumps = maxDoubleJumps;
        this.doubleJumpSound = doubleJumpSound;
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

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        e.setCancelled(true);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setFallDistance(0);

        if (remainingDoubleJumps > 0) {
            remainingDoubleJumps--;

            player.getWorld().playSound(player.getLocation(), doubleJumpSound, 1f, 1f);

            //Jumping when directly on the ground seems to transfer Y velocity to horizontal velocity, or simply diminish Y. (Go very fast forward)
            perfectJumped = false;
            if (!player.getLocation().subtract(0, 0.001, 0).getBlock().getType().isTransparent()) {
                perfectJumped = true;
            }

            jump();

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

        if (!player.equals(owner)) {
            return;
        }

        if (Utils.playerIsOnGround(owner)) {
            resetDoubleJumps();
        }
    }

    public void resetDoubleJumps() {
        remainingDoubleJumps = maxDoubleJumps;
        owner.setAllowFlight(true);
    }

    @EventHandler
    public void entityDamageEvent(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            e.setCancelled(true);
        }
    }

    protected abstract void jump();

    public void activate() {
        jump();
    }

}
