package SSM.Abilities;

import SSM.Ability;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

;

public class MilkSpiral extends Ability {

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
        this.rightClickActivate = true;
    }

    public void activate() {
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            int i = 0;
            Location ownerLocation = owner.getEyeLocation();
            @Override
            public void run() {
                World world = owner.getWorld();
                double pitch = (ownerLocation.getPitch() + 90) * Math.PI / 180;
                double yaw = (ownerLocation.getYaw() + 90) * Math.PI / 180;
                Vector v1 = new Vector(Math.cos(yaw) * Math.sin(pitch + Math.PI / 2), Math.cos(pitch + Math.PI / 2), Math.sin(yaw) * Math.sin(pitch + Math.PI / 2)).multiply(2);
                Vector v2 = new Vector(Math.cos(yaw + Math.PI / 2), 0, Math.sin(yaw + Math.PI / 2)).multiply(2);
                owner.sendMessage(String.format("%.2f", v1.getX()) + " " + String.format("%.2f", v1.getY()) + " " + String.format("%.2f", v1.getZ()));
                owner.sendMessage(String.format("%.2f", v2.getX()) + " " + String.format("%.2f", v2.getY()) + " " + String.format("%.2f", v2.getZ()));
                double iterations = spiralLength / iterationDiff;
                i++;
                Vector lookDirection = ownerLocation.getDirection().clone();
                Vector ownerPosition = ownerLocation.toVector().clone();
                double diff = 2 * Math.PI / particleDensity;
                double angle = i * diff + Math.PI / 2;
                Vector linePos = ownerPosition.add(lookDirection.multiply(initialDistance + i * iterationDiff));
                Vector circlePosFirst = v1.clone().multiply(Math.cos(angle)).add(v2.clone().multiply(Math.sin(angle)));
                Vector circlePosSecond = v1.clone().multiply(Math.cos(angle + Math.PI)).add(v2.clone().multiply(Math.sin(angle + Math.PI)));
                Vector finalPosFirst = linePos.clone().add(circlePosFirst);
                Vector finalPosSecond = linePos.clone().add(circlePosSecond);
                world.spawnParticle(Particle.FIREWORKS_SPARK, finalPosFirst.toLocation(world), 0);
                world.spawnParticle(Particle.FIREWORKS_SPARK, finalPosSecond.toLocation(world), 0);
                if (i >= iterations) {
                    Bukkit.getScheduler().cancelTask(task);
                }
            }
        }, 0L, 0L);
    }

}