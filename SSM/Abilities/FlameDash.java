package SSM.Abilities;

import SSM.Ability;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Utilities.DamageUtil;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.SlimeWatcher;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class FlameDash extends Ability implements OwnerRightClickEvent {

    private long time;
    private double duration = 0.75; //seconds
    private int task;
    private static Location oldLoc;
    private MobDisguise disg;
    private SlimeWatcher watcher;
    private boolean active;


    public FlameDash() {
        super();
        this.name = "Flame Dash";
        this.cooldownTime = 8;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        if (active) {
            explode();
            Bukkit.getScheduler().cancelTask(task);
            return;
        }
        Player player = e.getPlayer();
        checkAndActivate(player);
    }

    public void activate() {
        oldLoc = owner.getLocation();
        time = System.currentTimeMillis() + (int) (duration * 1000);

        disg = new MobDisguise(DisguiseType.MAGMA_CUBE);
        disg.setEntity(owner);
        watcher = (SlimeWatcher) disg.getWatcher();
        watcher.setInvisible(true);
        watcher.setCustomNameVisible(false);
        disg.startDisguise();

        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() >= time) {
                    Bukkit.getScheduler().cancelTask(task);
                    explode();
                }
                owner.setVelocity(oldLoc.getDirection().setY(0).normalize().multiply(1));
                owner.getWorld().playSound(owner.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1, 2);
                active = Bukkit.getScheduler().isCurrentlyRunning(task);

            }
        }, 0L, 1L);

    }

    private void explode() {
        active = false;
        watcher.setInvisible(false);
        watcher.setCustomNameVisible(true);
        disg.startDisguise();
        Location newLoc = owner.getLocation();
        double dist = oldLoc.distance(newLoc);
        List<Entity> nearby = owner.getNearbyEntities(2, 2, 2);
        nearby.remove(owner);
        for (Entity entity : nearby) {
            if (!(entity instanceof LivingEntity)) {
                continue;
            }
            LivingEntity target = (LivingEntity) entity;
            DamageUtil.dealDamage(owner, target, dist + 2, true, false);
        }
    }

}
