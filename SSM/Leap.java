package SSM;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;


import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class Leap extends Ability {

    protected double timeActive;
    protected boolean active;
    protected double duration = 50; // 2.5 seconds
    protected boolean endOnLand;
    protected double hitbox, power;
    int task, hitDetection, landDetection;


    public Leap() {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    protected void init(){
        owner.setVelocity(owner.getLocation().getDirection().multiply(power));
        active = true;
        hitDetection();
        landDetection();
        timeActive = 0;
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (!active){
                    Bukkit.getScheduler().cancelTask(task);
                }
                if (timeActive > duration){
                    active = false;
                    Bukkit.getScheduler().cancelTask(task);
                }
                timeActive++;
            }
        }, 0L, 1L);
    }

    private void hitDetection(){
        hitDetection = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (!active){
                    Bukkit.getScheduler().cancelTask(hitDetection);
                }
                List<Entity> nearby = owner.getNearbyEntities(hitbox, hitbox, hitbox);
                nearby.remove(owner);
                for (Entity entity : nearby){
                    if (!(entity instanceof LivingEntity)){
                        return;
                    }
                    LivingEntity target = (LivingEntity)entity;
                    onHit(target);
                    active = false;
                }
            }
        },0L, 1L);
    }

    private void landDetection(){
        landDetection = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (!active){
                    Bukkit.getScheduler().cancelTask(landDetection);
                }
                if (timeActive <= 5){
                    return; // Prevents slam moves from immediately ending if you use them on the ground.
                }
                Block block = owner.getLocation().subtract(0, -0.001, 0).getBlock();
                if (!block.isPassable()){
                    onLand();
                    owner.sendMessage("test");
                }
            }
        }, 0L, 1L);
    }

    public abstract void onLand();

    public abstract void onHit(LivingEntity target);






}



