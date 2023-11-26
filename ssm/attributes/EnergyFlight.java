package ssm.attributes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import ssm.attributes.doublejumps.DoubleJump;
import ssm.kits.Kit;
import ssm.managers.KitManager;
import ssm.utilities.BlocksUtil;
import ssm.utilities.ServerMessageType;
import ssm.utilities.Utils;
import ssm.utilities.VelocityUtil;

public class EnergyFlight extends Attribute {

    protected double energy_usage;
    protected boolean last_flight_state = false;
    protected long last_stop_flying_ms = 0;
    protected long flight_cooldown_ms = 0;
    protected boolean on_cooldown = false;

    public EnergyFlight(double energy_usage, long flight_cooldown_ms) {
        super();
        this.name = "Wither Flight";
        this.usage = AbilityUsage.PASSIVE;
        this.description = new String[] {
                ChatColor.RESET + "While you have energy you can use flight.",
                ChatColor.RESET + "When you stop flying you may not",
                ChatColor.RESET + "fly again until you touch the ground.",
                ChatColor.RESET + "",
                ChatColor.RESET + "" + ChatColor.AQUA + "Wither Flight uses Energy (Experience Bar)",
        };
        this.energy_usage = energy_usage;
        this.flight_cooldown_ms = flight_cooldown_ms;
        task = this.runTaskTimer(plugin, 0L, 0L);
    }

    @Override
    public void run() {
        if(owner == null) {
            task.cancel();
            return;
        }
        if(Utils.entityIsOnGround(owner)) {
            owner.setAllowFlight(true);
        }
        Kit kit = KitManager.getPlayerKit(owner);
        if(kit != null) {
            ExpCharge charge = kit.getAttributeByClass(ExpCharge.class);
            if(charge != null) {
                charge.enabled = !owner.isFlying();
            }
        }
        if(last_flight_state && !owner.isFlying()) {
            last_stop_flying_ms = System.currentTimeMillis();
            owner.getWorld().playSound(owner.getLocation(), Sound.ANVIL_LAND, 1.0F, 0.5F);
            owner.setAllowFlight(false);
            last_flight_state = owner.isFlying();
        }
        if(System.currentTimeMillis() - last_stop_flying_ms < flight_cooldown_ms) {
            owner.setFlying(false);
            on_cooldown = true;
            return;
        }
        if(on_cooldown) {
            Utils.sendAttributeMessage("You can use", name, owner, ServerMessageType.RECHARGE);
            on_cooldown = false;
        }
        if (owner.isFlying()) {
            owner.setExp((float) Math.max(owner.getExp() - energy_usage, 0));
        }
        last_flight_state = owner.isFlying();
        if(owner.getExp() <= 0) {
            owner.setFlying(false);
        }
    }

    @Override
    public void activate() {
        return;
    }

    public boolean canFly() {
        return !on_cooldown;
    }

    @EventHandler
    public void playerFlightEvent(PlayerToggleFlightEvent e) {
        if(!e.getPlayer().equals(owner)) {
            return;
        }
        if(on_cooldown) {
            long time_elapsed_ms = System.currentTimeMillis() - last_stop_flying_ms;
            Utils.sendAttributeMessage("You cannot use " + ChatColor.GREEN + name + ChatColor.GRAY + " for",
                    String.format("%.1f seconds", (flight_cooldown_ms - time_elapsed_ms) / 1000.0), owner, ServerMessageType.RECHARGE);
            e.setCancelled(true);
        }
    }

}
