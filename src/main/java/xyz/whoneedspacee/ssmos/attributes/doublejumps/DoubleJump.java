package xyz.whoneedspacee.ssmos.attributes.doublejumps;

import xyz.whoneedspacee.ssmos.attributes.Attribute;
import xyz.whoneedspacee.ssmos.events.SmashDamageEvent;
import xyz.whoneedspacee.ssmos.kits.Kit;
import xyz.whoneedspacee.ssmos.managers.KitManager;
import xyz.whoneedspacee.ssmos.utilities.Utils;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public abstract class DoubleJump extends Attribute {

    protected double height;
    protected double power;
    protected Sound double_jump_sound;
    protected long last_jump_time_ms = 0;
    protected long recharge_delay_ms = 0;

    public DoubleJump(double power, double height, Sound double_jump_sound) {
        super();
        this.power = power;
        this.height = height;
        this.double_jump_sound = double_jump_sound;
        this.runTaskTimer(plugin, 0L, 0L);
    }

    // Recharge doublejump
    @Override
    public void run() {
        if(owner == null) {
            return;
        }
        if(owner.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        Kit kit = KitManager.getPlayerKit(owner);
        if(kit != null && !kit.isActive()) {
            owner.setAllowFlight(false);
            return;
        }
        if(System.currentTimeMillis() - last_jump_time_ms < recharge_delay_ms) {
            return;
        }
        if(groundCheck()) {
            owner.setAllowFlight(true);
        }
    }

    @EventHandler
    public void playerFlightEvent(PlayerToggleFlightEvent e) {
        if(!e.getPlayer().equals(owner)) {
            return;
        }
        Player player = e.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        e.setCancelled(true);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setFallDistance(0);
        if(!check()) {
            return;
        }
        playDoubleJumpSound();
        checkAndActivate();
        last_jump_time_ms = System.currentTimeMillis();
    }

    @Override
    public void activate() {
        return;
    }

    public boolean groundCheck() {
        return Utils.entityIsOnBlock(owner);
    }

    public void playDoubleJumpSound() {
        owner.getWorld().playSound(owner.getLocation(), double_jump_sound, 1f, 1f);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void smashDamageEvent(SmashDamageEvent e) {
        if (e.getDamageCause() == EntityDamageEvent.DamageCause.FALL) {
            e.setCancelled(true);
        }
    }

    @Override
    public void setOwner(Player owner) {
        if(owner == null) {
            this.owner.setFlying(false);
            this.owner.setAllowFlight(false);
        }
        super.setOwner(owner);
    }

}
