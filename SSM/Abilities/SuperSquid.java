package SSM.Abilities;

import SSM.GameManagers.KitManager;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Utilities.VelocityUtil;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerInteractEvent;

public class SuperSquid extends Ability implements OwnerRightClickEvent {

    private int task = -1;
    private boolean active = false;

    public SuperSquid() {
        super();
        this.name = "Super Squid";
        this.cooldownTime = 8;
        this.usage = AbilityUsage.BLOCKING;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            int ticks = 0;

            @Override
            public void run() {
                ticks++;
                if (KitManager.getPlayerKit(owner) == null) {
                    Bukkit.getScheduler().cancelTask(task);
                    return;
                }
                if (!owner.isBlocking() || ticks >= 22) {
                    Bukkit.getScheduler().cancelTask(task);
                    KitManager.getPlayerKit(owner).setInvincible(false);
                    return;
                }
                KitManager.getPlayerKit(owner).setInvincible(true);
                VelocityUtil.setVelocity(owner, 0.6, 0.1, 1, true);
                owner.getWorld().playSound(owner.getLocation(), Sound.SPLASH2, 0.2f, 1f);
                owner.getWorld().playEffect(owner.getLocation(), Effect.STEP_SOUND, 8);
            }
        }, 0L, 0L);
    }

}
