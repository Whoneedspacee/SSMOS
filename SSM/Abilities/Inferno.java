package SSM.Abilities;

import SSM.Attributes.ExpCharge;
import SSM.GameManagers.KitManager;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Kits.Kit;
import SSM.Projectiles.FireProjectile;
import SSM.Projectiles.IronHookProjectile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class Inferno extends Ability implements OwnerRightClickEvent {

    private int inferno_task = - 1;
    protected float energy_per_fire = 0.035f;

    public Inferno() {
        super();
        this.name = "Inferno";
        this.usage = AbilityUsage.HOLD_BLOCKING;
        this.description = new String[] {
                ChatColor.RESET + "Releases a deadly torrent of flames,",
                ChatColor.RESET + "which ignite and damage opponents.",
        };
        this.runTaskTimer(plugin, 0L, 0L);
    }

    @Override
    public void run() {
        if(owner == null) {
            this.cancel();
            return;
        }
        Kit kit = KitManager.getPlayerKit(owner);
        if(kit == null) {
            return;
        }
        ExpCharge expCharge = kit.getAttributeByClass(ExpCharge.class);
        if(expCharge == null) {
            return;
        }
        expCharge.enabled = !owner.isBlocking();
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        if(Bukkit.getScheduler().isQueued(inferno_task) || Bukkit.getScheduler().isCurrentlyRunning(inferno_task)) {
            return;
        }
        checkAndActivate();
    }

    public void activate() {
        if(Bukkit.getScheduler().isQueued(inferno_task) || Bukkit.getScheduler().isCurrentlyRunning(inferno_task)) {
            Bukkit.getScheduler().cancelTask(inferno_task);
        }
        inferno_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(owner == null || !owner.isBlocking()) {
                    Bukkit.getScheduler().cancelTask(inferno_task);
                    return;
                }
                owner.setExp(owner.getExp() - energy_per_fire);
                if(owner.getExp() <= 0) {
                    Bukkit.getScheduler().cancelTask(inferno_task);
                    return;
                }
                FireProjectile projectile = new FireProjectile(owner, name);
                projectile.launchProjectile();
            }
        }, 0L, 0L);
    }

}