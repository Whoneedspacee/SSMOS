package xyz.whoneedspacee.ssmos.utilities;

import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class LineParticle {

    private final Location start;
    private Vector direction;
    private Location lastLocation;

    private double curRange;
    private final double incrementedRange;
    private final double maxRange;

    private boolean ignoreAllBlocks;

    private final EnumParticle particleType;
    private final List<Player> toDisplay;

    public LineParticle(Location start, Vector direction, double incrementedRange, double maxRange, EnumParticle particleType, List<Player> toDisplay) {
        this(start, null, direction, incrementedRange, maxRange, particleType, toDisplay);
    }

    public LineParticle(Location start, Location end, Vector direction, double incrementedRange, double maxRange, EnumParticle particleType, List<Player> toDisplay) {
        this.start = start;
        this.direction = direction;
        this.lastLocation = start;

        this.curRange = 0;
        this.incrementedRange = incrementedRange;
        this.maxRange = maxRange;

        this.particleType = particleType;
        this.toDisplay = toDisplay;

        if (this.direction == null) {
            this.direction = end.clone().subtract(start.clone()).toVector().normalize();
        }
    }

    public boolean update() {
        boolean done = curRange > maxRange;
        Location newTarget = start.clone().add(direction.clone().multiply(curRange));

        if (newTarget.getY() < 0) {
            newTarget.add(0, 0.2, 0);
        }

        lastLocation = newTarget;

        if (!ignoreAllBlocks && newTarget.getBlock().getType().isSolid()) {
            done = true;
        }

        curRange += incrementedRange;

        Utils.playParticle(particleType, newTarget,
                0, 0, 0, 0, 1, 96, toDisplay);

        return done;
    }

    public void setIgnoreAllBlocks(boolean b) {
        ignoreAllBlocks = b;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public Location getDestination() {
        return lastLocation.subtract(direction);
    }
}
