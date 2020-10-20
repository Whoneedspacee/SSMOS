package SSM.Abilities;

import SSM.Ability;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class MilkSpiral extends Ability implements OwnerRightClickEvent {

    // length in blocks
    private int spiralLength = 40;
    // particle density of a the circle
    private int particleDensity = 40;
    // amount the center of our circle moves per iteration
    private double iterationDiff = 0.2;
    // initial distance
    private double initialDistance = 2;
    // task for spiral
    private int task;

    public MilkSpiral() {
        super();
        this.name = "Milk Spiral";
        this.cooldownTime = 0.5;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        checkAndActivate(player);
    }

    public void activate() {
        Bukkit.getScheduler().cancelTask(task);
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            int i = 0;
            World world = owner.getWorld();
            Location ownerLocation = owner.getEyeLocation();
            Vector ownerPosition = ownerLocation.toVector();
            Vector lookDir = ownerLocation.getDirection();
            double iterations = spiralLength / iterationDiff;
            Vector v1 = new Vector(-lookDir.getZ(), 0, lookDir.getX()).normalize();
            Vector v2 = lookDir.getCrossProduct(v1);
            @Override
            public void run() {
                if (i >= iterations) {
                    Bukkit.getScheduler().cancelTask(task);
                }
                double angle = i * (2 * Math.PI / particleDensity);
                Vector linePos = ownerPosition.clone().add(lookDir.clone().multiply(initialDistance + i * iterationDiff));
                Vector circlePosFirst = v1.clone().multiply(Math.cos(angle)).add(v2.clone().multiply(Math.sin(angle)));
                Vector circlePosSecond = circlePosFirst.clone().multiply(-1);
                Vector finalPosFirst = linePos.clone().add(circlePosFirst);
                Vector finalPosSecond = linePos.clone().add(circlePosSecond);
                world.spawnParticle(Particle.FIREWORKS_SPARK, finalPosFirst.toLocation(world), 0, 0, 0, 0, 0, null, true);
                world.spawnParticle(Particle.FIREWORKS_SPARK, finalPosSecond.toLocation(world), 0, 0, 0, 0, 0, null, true);
                i++;
            }
        }, 0L, 0L);
    }

}