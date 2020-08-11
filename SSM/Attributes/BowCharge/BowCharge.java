package SSM.Attributes.BowCharge;

import SSM.Attribute;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.projectiles.ProjectileSource;

public abstract class BowCharge extends Attribute {

    double delay;
    double rate;
    int maxCharge;
    int charge = 0;

    public BowCharge(double delay, double rate, int maxCharge) {
        super();
        this.name = "Bow Charge";
        this.delay = delay;
        this.rate = rate;
        this.maxCharge = maxCharge;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void drawBow(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (player != owner) {
            return;
        }
        if (player.getInventory().getItemInMainHand().getType() != Material.BOW) {
            return;
        }
        if (!player.getInventory().contains(Material.ARROW)) {
            return;
        }
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            finishFiring();
            task = Bukkit.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
                @Override
                public void run() {
                    if (charge < maxCharge) {
                        charge++;
                        incrementedCharge();
                    }
                }
            }, (long) (delay * 20), (long) (rate * 20));
        }
    }

    @EventHandler
    public void checkFiredBow(EntityShootBowEvent e) {
        Entity fired = e.getProjectile();
        if (!(fired instanceof Arrow)) {
            return;
        }
        Arrow arrow = (Arrow) fired;
        ProjectileSource source = arrow.getShooter();
        if (!(source instanceof Player)) {
            return;
        }
        Player player = (Player) source;
        if (player != owner) {
            return;
        }
        firedBow(arrow);
        finishFiring();
    }

    @EventHandler
    public void switchedWeapon(PlayerItemHeldEvent e) {
        Player player = e.getPlayer();
        if (player != owner) {
            return;
        }
        // finish firing for visuals but changing the charge can cause issues so set it back
        // the bow can fire sometimes after this event is called which resets the charges before they should be
        int storedCharge = charge;
        finishFiring();
        charge = storedCharge;
    }

    public void incrementedCharge() {
        owner.setExp(Math.min(0.9999F, (float) charge / maxCharge));
        owner.playSound(owner.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F + 0.1F * charge);
    }

    public abstract void firedBow(Arrow p);

    public void finishFiring() {
        cancelTask();
        owner.setExp(0);
        charge = 0;
    }

}

