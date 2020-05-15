package SSM;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class Leap extends Ability {

    protected HashMap<UUID, Boolean> activity = new HashMap<>();
    protected HashMap<UUID, Long> timerList = new HashMap<>();
    protected boolean endOnLand, timed;
    protected double activeTime = 5.0, hitbox, power;


    public Leap() {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void activate() {
        // This does nothing.
    }

    public void onLand(){

    }
    public void onHit(LivingEntity target){

    }

    @EventHandler
    public void hitbox(PlayerMoveEvent e){
        Player player = e.getPlayer();
        if (activity.get(player.getUniqueId()) == null){
            return;
        }
        List<Entity> nearby = owner.getNearbyEntities(hitbox-5, hitbox-5, hitbox-5);
        nearby.remove(owner);
        if (nearby.isEmpty()){
            return;
        }
        if (!(nearby.get(0) instanceof LivingEntity)){
            return;
        }
        LivingEntity target = (LivingEntity)nearby.get(0);
        onHit(target);
        activity.remove(player.getUniqueId());
        timerList.remove(player.getUniqueId());

        /*
        Above checks for hitbox, then runs a "onHit" method.
        */

    }
    @EventHandler
    public void whenEnd(PlayerMoveEvent e){
        Player player = e.getPlayer();
        if (activity.get(player.getUniqueId()) == null){
            return;
        }
        if ((System.currentTimeMillis() - (timerList.get(player.getUniqueId()) - activeTime*100L)) < 200){
            return;
        }
        if (!(player.getLocation().subtract(0, 0.001, 0).getBlock().isPassable()) && (activity.get(player.getUniqueId()) != null)){
            onLand();
            if (endOnLand){
                activity.remove(player.getUniqueId());
                timerList.remove(player.getUniqueId());
            }
        }
        if (timed){
            if (System.currentTimeMillis() < timerList.get(player.getUniqueId())){
                return;
            }else{
                timerList.remove(player.getUniqueId());
                activity.remove(player.getUniqueId());
            }
        }


    }


}



