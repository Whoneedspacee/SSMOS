package ssm.abilities.original;

import ssm.abilities.Ability;
import ssm.managers.ownerevents.OwnerRightClickEvent;
import ssm.projectiles.original.BileProjectile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerInteractEvent;

public class BileBlaster extends Ability implements OwnerRightClickEvent {

    private int spew_task = -1;
    private long spew_starting_time_ms = 0;
    protected int projectiles_per_tick = 1;
    protected long spew_duration_ms = 2000;

    public BileBlaster() {
        super();
        this.name = "Spew Bile";
        this.item_name = "Bile Blaster";
        this.cooldownTime = 10;
        this.description = new String[]{
                ChatColor.RESET + "Spew up your dinner from last night.",
                ChatColor.RESET + "Deals damage and knockback to enemies.",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        if(Bukkit.getScheduler().isQueued(spew_task) || Bukkit.getScheduler().isCurrentlyRunning(spew_task)) {
            Bukkit.getScheduler().cancelTask(spew_task);
        }
        spew_starting_time_ms = System.currentTimeMillis();
        spew_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(owner == null) {
                     Bukkit.getScheduler().cancelTask(spew_task);
                     return;
                }
                if(System.currentTimeMillis() - spew_starting_time_ms >= 2000) {
                    Bukkit.getScheduler().cancelTask(spew_task);
                    return;
                }
                if(Math.random() > 0.85) {
                    owner.getWorld().playSound(owner.getLocation(), Sound.BURP, 1f, (float) (Math.random() + 0.5));
                }
                for(int i = 0; i < projectiles_per_tick; i++) {
                    BileProjectile projectile = new BileProjectile(owner, name);
                    projectile.launchProjectile();
                }
            }
        }, 0L, 0L);
    }

}