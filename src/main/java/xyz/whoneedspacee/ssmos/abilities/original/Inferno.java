package xyz.whoneedspacee.ssmos.abilities.original;

import xyz.whoneedspacee.ssmos.managers.ownerevents.OwnerRightClickEvent;
import xyz.whoneedspacee.ssmos.projectiles.original.FireProjectile;
import xyz.whoneedspacee.ssmos.abilities.Ability;
import xyz.whoneedspacee.ssmos.attributes.ExpCharge;
import xyz.whoneedspacee.ssmos.managers.KitManager;
import xyz.whoneedspacee.ssmos.kits.Kit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.whoneedspacee.ssmos.attributes.Attribute;

public class Inferno extends Ability implements OwnerRightClickEvent {

    private int inferno_task = - 1;
    public float energy_per_fire = 0.035f;
    public float exp_to_use = 0f;
    public int fire_ticks_added = 10;

    public Inferno() {
        super();
        this.name = "Inferno";
        this.usage = Attribute.AbilityUsage.HOLD_BLOCKING;
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
        if(owner.getExp() < exp_to_use) {
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
                FireProjectile projectile = new FireProjectile(owner, name, fire_ticks_added);
                projectile.launchProjectile();
            }
        }, 0L, 0L);
    }

}