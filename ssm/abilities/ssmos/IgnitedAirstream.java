package ssm.abilities.ssmos;

import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import ssm.Main;
import ssm.abilities.Ability;
import ssm.managers.ownerevents.OwnerRightClickEvent;

public class IgnitedAirstream extends Ability implements OwnerRightClickEvent {

    private int fly_task = -1;
    private boolean second_use = false;

    public IgnitedAirstream() {
        super();
        this.name = "Ignited Airstream";
        this.cooldownTime = 8;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        if (Bukkit.getScheduler().isCurrentlyRunning(fly_task) || Bukkit.getScheduler().isQueued(fly_task)){
            if (!second_use){
                second_use = true;
                activate();
                return;
            }
        }
        checkAndActivate();
    }

    public void activate() {
        Bukkit.getScheduler().cancelTask(fly_task);
        fly_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
            private Vector dir = owner.getLocation().getDirection();
            private final long start = System.currentTimeMillis();

            @Override
            public void run() {
                owner.setVelocity(dir.clone().multiply(1.05));
                if (System.currentTimeMillis() > start + 250) {
                    Bukkit.getScheduler().cancelTask(fly_task);
                    if (!second_use) {
                        activate();
                        second_use = true;
                    } else {
                        second_use = false;
                    }
                }
            }
        }, 0L, 0L);
    }


}