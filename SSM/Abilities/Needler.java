package SSM.Abilities;

import SSM.Ability;
import SSM.EntityProjectile;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;

public class Needler extends Ability implements OwnerRightClickEvent {

    private int needleAmount = 6;
    private int fired = 0;
    private BukkitTask taskID;

    public Needler() {
        super();
        this.name = "Needler";
        this.cooldownTime = 2.5;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        taskID = Bukkit.getScheduler().runTaskTimer(plugin, () ->
        {
            if(!owner.isBlocking() || fired >= 6) {
                fired = 0;
                Bukkit.getScheduler().cancelTask(taskID.getTaskId());
                return;
            }
            Arrow arrow = owner.launchProjectile(Arrow.class);
            arrow.setCustomName("Needler");
            arrow.setMetadata("Needler", new FixedMetadataValue(plugin, 1));
            arrow.setVelocity(owner.getLocation().getDirection().multiply(2.4));
            owner.playSound(owner.getLocation(), Sound.SPIDER_IDLE, 10L, 1L);
            fired++;
        }, 0L, 2L);
    }
}
