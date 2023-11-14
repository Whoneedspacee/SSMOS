package SSM.Abilities;

import SSM.Events.SmashDamageEvent;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.SSM;
import SSM.Utilities.DamageUtil;
import SSM.Utilities.Utils;
import SSM.Utilities.VelocityUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class SonicHurr extends Ability implements OwnerRightClickEvent {

    protected double damage = 11;
    protected double hitbox_size = 3;
    protected double velocity_factor = 0.5;
    protected double distance = 6;

    public SonicHurr() {
        super();
        this.name = "Sonic Hurr";
        this.cooldownTime = 9;
        this.description = new String[]{
                ChatColor.RESET + "Screech at the top of your lungs piercing players ears",
                ChatColor.RESET + "dealing damage and knockback in front of you.",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        Location location = owner.getEyeLocation();
        location.add(location.getDirection());
        owner.getWorld().playSound(owner.getLocation(), Sound.VILLAGER_IDLE, 1.5f, 1.2f);
        new SpiralEffect(1, 2, 6 * 10, location) {
            @Override
            public void playParticle(Location location) {
                Utils.playParticle(EnumParticle.CLOUD, location,
                        0, 0, 0, 0, 1, 96, location.getWorld().getPlayers());
                if (Math.random() < 0.05) {
                    Utils.playParticle(EnumParticle.VILLAGER_ANGRY, location,
                            0.3f, 0.3f, 0.3f, 0, 1, 96, location.getWorld().getPlayers());
                }
            }
        }.start();
        Location center = location.clone().add(location.getDirection().multiply(distance / 2D)).subtract(0, 0.5, 0);
        for(Player player : Utils.getNearby(center, hitbox_size)) {
            if(player.equals(owner)) {
                continue;
            }
            if(!DamageUtil.canDamage(player, owner, damage)) {
                continue;
            }
            double scale = 1 - owner.getLocation().distance(player.getLocation()) / distance / 2;
            SmashDamageEvent smashDamageEvent = new SmashDamageEvent(player, owner, scale * damage);
            smashDamageEvent.multiplyKnockback(0);
            smashDamageEvent.setIgnoreDamageDelay(true);
            smashDamageEvent.setReason(name);
            smashDamageEvent.callEvent();
            Vector trajectory = player.getLocation().toVector().subtract(owner.getLocation().toVector()).normalize();
            VelocityUtil.setVelocity(player, trajectory, scale * velocity_factor, false, 0, 0.5, 0.8, true);
        }
    }

    public static interface Callback<T> {
        public void run(T data);
    }

    public static class EffectLocation {

        private Location _location;
        private Entity _entity;

        public EffectLocation(Location location) {
            _location = location;
            _entity = null;
        }

        public EffectLocation(Entity entity) {
            _location = entity.getLocation();
            _entity = entity;
        }

        public Location getLocation() {
            if (_entity != null && _entity.isValid() && !_entity.isDead())
                return _entity.getLocation().clone();
            if (_location != null)
                return _location.clone();
            return null;
        }

        public Location getFixedLocation() {
            if (_location != null)
                return _location.clone();
            return null;
        }

    }

    public static abstract class Effect {

        private int _ticksToRun = 20, _ticks = 0;
        private BukkitTask _task;
        protected long _period = 1;
        protected EffectLocation _effectLocation;
        private EffectLocation _targetLocation;
        private boolean _running = false;
        private Callback<Effect> _callback;

        public Effect(int ticks, EffectLocation effectLocation) {
            _ticksToRun = ticks;
            _effectLocation = effectLocation;
        }

        public Effect(int ticks, EffectLocation effectLocation, long delay) {
            _ticksToRun = ticks;
            _effectLocation = effectLocation;
            _period = delay;
        }

        public void start() {
            onStart();
            _running = true;
            _task = Bukkit.getScheduler().runTaskTimer(SSM.getInstance(), () ->
            {
                runEffect();
                update();
            }, 1, _period);
        }

        public void stop() {
            _running = false;
            _task.cancel();

            if (_callback != null) {
                _callback.run(this);
            }

            onStop();
        }

        public void onStart() {
        }

        public void onStop() {
        }

        private void update() {
            if (++_ticks == _ticksToRun) {
                _task.cancel();
            }
        }

        public boolean isRunning() {
            return _running;
        }

        public abstract void runEffect();

        public void setTargetLocation(EffectLocation effectLocation) {
            _targetLocation = effectLocation;
        }

        public EffectLocation getTargetLocation() {
            return _targetLocation;
        }

        public EffectLocation getEffectLocation() {
            return _effectLocation;
        }

        public void setCallback(Callback<Effect> callback) {
            _callback = callback;
        }

    }

    public abstract class SpiralEffect extends Effect {

        private static final double DELTA_THETA = Math.PI / 20D;
        private static final double DELTA_Z = 0.1;
        private static final double DELTA_RADIUS = 0.05;

        private final double _maxRadius;
        private final int _iterations;

        private double _radius, _theta, _z;

        public SpiralEffect(int ticks, double maxRadius, Location location) {
            this(ticks, maxRadius, 1, location);
        }

        public SpiralEffect(int ticks, double maxRadius, int iterations, Location location) {
            super(ticks, new EffectLocation(location));

            _maxRadius = maxRadius;
            _iterations = iterations;
        }

        @Override
        public void runEffect() {
            for (int i = 0; i < _iterations; i++) {
                double x = _radius * Math.cos(_theta);
                double y = _radius * Math.sin(_theta);
                double z = _z += DELTA_Z;

                Location location = getEffectLocation().getFixedLocation();

                location.add(x, y, z);

                rotateDirection(location);

                location.subtract(x * 2, y * 2, 0);

                rotateDirection(location);

                _theta += DELTA_THETA;

                if (_radius < _maxRadius) {
                    _radius += DELTA_RADIUS;
                }
            }
        }

        private void rotateDirection(Location location) {
            Location fixedLocation = getEffectLocation().getFixedLocation();
            Vector vector = location.toVector().subtract(fixedLocation.toVector());

            rotateAroundXAxis(vector, Math.toRadians(location.getPitch()));
            rotateAroundYAxis(vector, Math.toRadians(location.getYaw()));

            playParticle(fixedLocation.add(vector));
        }

        public static void rotateAroundXAxis(Vector vec, double angle) {
            double y = vec.getY(), z = vec.getZ(), sin = Math.sin(angle), cos = Math.cos(angle);
            vec.setY(y * cos - z * sin).setZ(y * sin + z * cos);
        }

        public static void rotateAroundYAxis(Vector vec, double angle) {
            double x = vec.getX(), z = vec.getZ(), sin = Math.sin(angle), cos = Math.cos(angle);
            vec.setX(x * cos - z * sin).setZ(x * sin + z * cos);
        }

        public abstract void playParticle(Location location);
    }

}