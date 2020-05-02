package SSM.Abilities;

import SSM.Ability;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.FlagWatcher;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.SlimeWatcher;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import javax.xml.bind.ValidationException;
import java.util.List;
import me.libraryaddict.disguise.LibsDisguises;
import me.libraryaddict.*;

public class FlameDash extends Ability {

    int DashLength = 19;
    int Dash = -1;
    int i = 0;
    int cancel = 0;
    double directionX = 0;
    double directionZ = 0;
    boolean active = false;
    int damage = 0;
    int early = 0;
    int cancelable = -1;
    int size = 1;

    public FlameDash() {
        super();
        this.name = "Flame Dash";
        this.cooldownTime = 8;
        this.rightClickActivate = true;
    }

    public void activate() {
        active = false;
        cancel = 0;
        DashLength = 19;
        early = 0;
        i = 0;
        MobDisguise disg = new MobDisguise(DisguiseType.MAGMA_CUBE);
        disg.setEntity(owner);
        SlimeWatcher watcher = (SlimeWatcher) disg.getWatcher();
        watcher.setInvisible(true);
        watcher.setCustomNameVisible(false);
        watcher.setSize(size);
        disg.startDisguise();
        directionX = owner.getLocation().getDirection().getX();
        directionZ = owner.getLocation().getDirection().getZ();
        damage = 3;

        Dash = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                early++;
                i++;
                owner.setVelocity(new Vector(directionX, 0, directionZ));
                owner.getWorld().spawnParticle(Particle.FLAME, owner.getLocation(), 1);
                owner.getWorld().spawnParticle(Particle.FLAME, owner.getLocation(), 1);
                owner.getWorld().playSound(owner.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1, 2);
                if (early >= DashLength) {
                    stop();
                }
                if (i >= 3.16666667) {
                    damage = 4;
                }
                if (i >= 6.33333334) {
                    damage = 5;
                }
                if (i >= 9.50000001) {
                    damage = 6;
                }
                if (i >= 12.6666667) {
                    damage = 7;
                }
                if (i >= 15.8333334) {
                    damage = 8;
                }
                if (i >= 18) {
                    damage = 9;
                }
            }
        }, 0, 1);

        cancelable = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (i >= 1.5){
                    active = true;
                    Bukkit.getScheduler().cancelTask(cancelable);
                }
            }
        },0,1);
    }

    @EventHandler
    public void onEarlyExplode(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        player.getInventory();
        if (e.getAction() == Action.RIGHT_CLICK_AIR && active && player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("Flame Dash") || e.getAction() == Action.RIGHT_CLICK_BLOCK && active && player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("Flame Dash")) {
            active = false;
            stop();
        }
    }

    private void stop() {
        active = false;
        Bukkit.getScheduler().cancelTask(Dash);
        run2();
    }

    public void run2() {
        active = false;
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1, 1);
        owner.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, owner.getLocation(), 1);
        List<Entity> canHit = owner.getNearbyEntities(2, 2, 2);
        canHit.remove(owner);
        MobDisguise disg = new MobDisguise(DisguiseType.MAGMA_CUBE);
        disg.setEntity(owner);
        SlimeWatcher watcher = (SlimeWatcher) disg.getWatcher();
        watcher.setInvisible(false);
        watcher.setCustomNameVisible(true);
        watcher.setCustomName(""+owner.getName());
        watcher.setSize(size);
        disg.startDisguise();

        for (Entity entity : canHit) {
            if ((entity instanceof LivingEntity)) {
                ((LivingEntity) entity).damage(damage);
                Vector target = entity.getLocation().toVector();
                Vector player = owner.getLocation().toVector();
                Vector pre = target.subtract(player);
                Vector velocity = pre.normalize().multiply(1.5);
                entity.setVelocity(new Vector(velocity.getX(), 0.4, velocity.getZ()));
            }
        }
    }
}