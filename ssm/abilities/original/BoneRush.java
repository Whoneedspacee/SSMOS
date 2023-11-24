package ssm.abilities.original;

import ssm.abilities.Ability;
import ssm.managers.CooldownManager;
import ssm.managers.ownerevents.OwnerRightClickEvent;
import ssm.projectiles.BoneProjectile;
import ssm.utilities.VelocityUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerInteractEvent;

public class BoneRush extends Ability implements OwnerRightClickEvent {

    private int rush_task = -1;
    protected long duration_ms = 1500;
    protected double y_limit = 0.25;
    protected int projectiles_per_tick = 6;

    public BoneRush() {
        super();
        this.name = "Bone Rush";
        this.cooldownTime = 10;
        this.description = new String[] {
                ChatColor.RESET + "Charge forth in a deadly wave of bones.",
                ChatColor.RESET + "Bones deal small damage and knockback.",
                ChatColor.RESET + "",
                ChatColor.RESET + "Holding Crouch will prevent you from",
                ChatColor.RESET + "moving forward with the bones.",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        if(Bukkit.getScheduler().isQueued(rush_task) || Bukkit.getScheduler().isCurrentlyRunning(rush_task)) {
            Bukkit.getScheduler().cancelTask(rush_task);
        }
        rush_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(owner == null || !owner.isValid()) {
                    Bukkit.getScheduler().cancelTask(rush_task);
                    return;
                }
                if(CooldownManager.getInstance().getTimeElapsedFor(BoneRush.this, owner) >= duration_ms) {
                    Bukkit.getScheduler().cancelTask(rush_task);
                    return;
                }
                owner.getWorld().playSound(owner.getLocation(), Sound.SKELETON_HURT, 0.4f, (float) (Math.random() + 1));
                double limit = y_limit;
                if(!owner.isSneaking()) {
                    VelocityUtil.setVelocity(owner, owner.getLocation().getDirection(), 0.6, false, 0, 0, limit, false);
                }
                for(int i = 0; i < projectiles_per_tick; i++) {
                    BoneProjectile projectile = new BoneProjectile(owner, name);
                    projectile.launchProjectile();
                }
            }
        }, 0L, 0L);
    }

}