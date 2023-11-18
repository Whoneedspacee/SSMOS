package ssm.abilities;

import ssm.events.SmashDamageEvent;
import ssm.managers.ownerevents.OwnerRightClickEvent;
import ssm.utilities.DamageUtil;
import ssm.utilities.Utils;
import ssm.utilities.VelocityUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class MilkSpiral extends Ability implements OwnerRightClickEvent {

    private int spiral_task = -1;
    private long spiral_start_time_ms = 0;
    protected long spiral_duration_ms = 3000;
    protected long velocity_duration_ms = 1800;
    protected double hitbox_radius = 2;
    protected double damage = 5;
    protected HashMap<Player, Integer> times_hit = new HashMap<Player, Integer>();
    protected int max_times_hit = 2;
    protected HashMap<Player, Long> last_damage_time = new HashMap<Player, Long>();
    protected long damage_cooldown_ms = 250;

    public MilkSpiral() {
        super();
        this.name = "Milk Spiral";
        this.cooldownTime = 9;
        this.description = new String[] {
                ChatColor.RESET + "Spray out a spiral of milk, propelling",
                ChatColor.RESET + "yourself forwards through it. Deals damage",
                ChatColor.RESET + "to opponents it collides with.",
                ChatColor.RESET + "",
                ChatColor.RESET + "Crouching cancels propulsion.",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        if (Bukkit.getScheduler().isQueued(spiral_task) || Bukkit.getScheduler().isCurrentlyRunning(spiral_task)) {
            Bukkit.getScheduler().cancelTask(spiral_task);
        }
        spiral_start_time_ms = System.currentTimeMillis();
        times_hit.clear();
        last_damage_time.clear();
        spiral_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            private final Vector direction = owner.getLocation().getDirection();
            private final Location spiral_location = owner.getLocation().add(new Vector(0, 1, 0)).add(direction.clone().multiply(2));
            private boolean do_velocity = true;

            @Override
            public void run() {
                if (owner == null || System.currentTimeMillis() - spiral_start_time_ms >= spiral_duration_ms) {
                    Bukkit.getScheduler().cancelTask(spiral_task);
                    return;
                }
                if (owner.isSneaking() || System.currentTimeMillis() - spiral_start_time_ms >= velocity_duration_ms) {
                    do_velocity = false;
                }
                if (do_velocity) {
                    VelocityUtil.setVelocity(owner, direction.clone().add(new Vector(0, 0.1, 0)).normalize().multiply(0.45));
                }
                Location old_location = spiral_location.clone();
                double total_distance = 0.7;
                spiral_location.add(direction.clone().multiply(total_distance));
                // Make the first vector that represents the x axis of the circle in the spiral
                // Set y to 0 and rotate 90 degrees to get the middle part of the circle on the spiral
                Vector circle_first = new Vector(-direction.getZ(), 0, direction.getX()).normalize();
                // Cross product gets the perpendicular vector which is the y axis of the circle
                Vector circle_second = direction.clone().crossProduct(circle_first).normalize();
                // Now that we have our circle we can make the particles
                double speed = 3;
                double radius = 1.5;
                double theta = owner.getTicksLived() / speed;
                boolean first = true;
                double total_added_distance = 0;
                while(total_added_distance < total_distance) {
                    Location first_particle = old_location.clone().add(getCirclePoint(circle_first, circle_second, theta, radius));
                    Location second_particle = old_location.clone().add(getCirclePoint(circle_first, circle_second, theta + Math.PI, radius));
                    if(first) {
                        first_particle.getWorld().playSound(first_particle, Sound.SPLASH, 0.2f, 0.75f);
                        second_particle.getWorld().playSound(second_particle, Sound.SPLASH, 0.2f, 0.75f);
                        first = false;
                    }
                    Utils.playParticle(EnumParticle.FIREWORKS_SPARK, first_particle,
                            0, 0, 0, 0, 1, 96, first_particle.getWorld().getPlayers());
                    Utils.playParticle(EnumParticle.FIREWORKS_SPARK, second_particle,
                            0, 0, 0, 0, 1, 96, second_particle.getWorld().getPlayers());
                    double distance = total_distance / 4;
                    old_location.add(direction.clone().multiply(distance));
                    // Multiply the normal theta change by the percent distance moved so we can simulate the in between
                    theta += (1 / speed) * (distance / total_distance);
                    total_added_distance += distance;
                }
                // Damage Portion
                for(Player player : owner.getWorld().getPlayers()) {
                    if(!DamageUtil.canDamage(player, owner)) {
                        continue;
                    }
                    if(last_damage_time.get(player) != null && System.currentTimeMillis() - last_damage_time.get(player) < damage_cooldown_ms) {
                        continue;
                    }
                    if(times_hit.get(player) != null && times_hit.get(player) >= max_times_hit) {
                        continue;
                    }
                    if(player.getLocation().add(0, 1.5, 0).distance(spiral_location) >= hitbox_radius) {
                        continue;
                    }
                    last_damage_time.put(player, System.currentTimeMillis());
                    times_hit.putIfAbsent(player, 0);
                    times_hit.put(player, times_hit.get(player) + 1);
                    Utils.playParticle(EnumParticle.FIREWORKS_SPARK, player.getLocation().add(0, 1, 0),
                            0.2f, 0.2f, 0.2f, 0.3f, 30, 96, player.getWorld().getPlayers());
                    player.getWorld().playSound(player.getLocation(), Sound.SPLASH, 0.2f, 2f);
                    SmashDamageEvent smashDamageEvent = new SmashDamageEvent(player, owner, damage);
                    smashDamageEvent.setIgnoreDamageDelay(true);
                    smashDamageEvent.setReason(name);
                    smashDamageEvent.callEvent();
                }
            }
        }, 0L, 0L);
    }

    protected Vector getCirclePoint(Vector circle_first, Vector circle_second, double theta, double radius) {
        Vector particle_offset = circle_first.clone().multiply(Math.cos(theta) * radius);
        particle_offset.add(circle_second.clone().multiply(Math.sin(theta) * radius));
        return particle_offset;
    }

}