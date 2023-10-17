package SSM.Abilities;

import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Utilities.DamageUtil;
import SSM.Utilities.Utils;
import SSM.Utilities.VelocityUtil;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

public class Fissure extends Ability implements OwnerRightClickEvent {

    private int fissureLength = 14;
    private int task = -1;
    private int remove_task = -1;
    private Material type;
    private HashMap<Block, Integer> blocks = new HashMap<Block, Integer>();
    private List<Entity> already_hit = new ArrayList<Entity>();

    public Fissure() {
        super();
        this.name = "Fissure";
        this.cooldownTime = 8;
        remove_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                for (Block remove : blocks.keySet()) {
                    blocks.put(remove, blocks.get(remove) - 1);
                    if (blocks.get(remove) <= 0) {
                        remove.setType(Material.AIR);
                    }
                }
                blocks.entrySet().removeIf(entry -> (entry.getValue() <= 0));
            }
        }, 0L, 1L);
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        if (!Utils.entityIsOnGround(owner)) {
            owner.sendMessage("You cannot use Fissure while airborne.");
            return;
        }
        checkAndActivate();
    }

    public void activate() {
        already_hit.clear();
        Location startloc = owner.getLocation().subtract(0, 0.4, 0);
        Vector dir = startloc.getDirection().setY(0).normalize().multiply(0.1);
        Location checkloc = startloc.clone();
        List<Block> path = new ArrayList<Block>();
        while (Utils.getXZDistance(startloc, checkloc) < 14) {
            checkloc.add(dir);

            Block block = checkloc.getBlock();

            if (block.equals(startloc.getBlock())) {
                continue;
            }
            if (path.contains(block)) {
                continue;
            }
            if ((block.getRelative(BlockFace.UP).getType().isSolid())) {
                checkloc.add(0, 1, 0);
                block = checkloc.getBlock();
                if (block.getRelative(BlockFace.UP).getType().isSolid()) {
                    break;
                }
            } else if (!block.getType().isSolid()) {
                checkloc.add(0, -1, 0);
                block = checkloc.getBlock();

                if (!block.getType().isSolid()) {
                    return;
                }
            }
            if (block.getLocation().add(0.5, 0.5, 0.5).distance(checkloc) > 0.5) {
                continue;
            }
            path.add(block);
            if (path.size() > 3) {
                path.add(block.getRelative(0, 1, 0));
            }
            checkloc.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
            for (Player player : block.getWorld().getPlayers()) {
                if (!player.equals(owner)) {
                    if (block.getLocation().add(0.5, 0.5, 0.5).distance(player.getLocation()) < 1.5) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 80, 1));
                    }
                }
            }
        }

        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            ListIterator<Block> iterator = path.listIterator();
            Block next_block = null;
            Block last_block = null;

            @Override
            public void run() {
                if (!iterator.hasNext()) {
                    stop();
                    return;
                }
                next_block = iterator.next().getRelative(0, 1, 0);
                // New column, skip a tick
                if (last_block != null) {
                    if (last_block.getX() != next_block.getX() || last_block.getZ() != next_block.getZ()) {
                        iterator.previous();
                        last_block = null;
                        return;
                    }
                }
                next_block.setType(next_block.getRelative(BlockFace.DOWN).getType());
                next_block.setData(next_block.getRelative(BlockFace.DOWN).getData());
                blocks.put(next_block, 200);
                next_block.getWorld().playEffect(next_block.getLocation(), Effect.STEP_SOUND, next_block.getTypeId());
                // This is an upper block, don't do damage calcs
                if (last_block != null && last_block.getX() == next_block.getX() && last_block.getZ() == next_block.getZ()) {
                    blocks.put(next_block, 180);
                    last_block = next_block;
                    return;
                }
                // Hit Detection & Bottom Block Handling
                Collection<Entity> hit_entites = next_block.getWorld().getNearbyEntities(next_block.getLocation().add(0.5, 0.5, 0.5), 1.5, 1.5, 1.5);
                for (Entity hit : hit_entites) {
                    if (!(hit instanceof LivingEntity) || hit.equals(owner) || already_hit.contains(hit)) {
                        continue;
                    }
                    LivingEntity living = (LivingEntity) hit;
                    int distance = (int) hit.getLocation().distance(path.get(0).getLocation());
                    DamageUtil.damage(living, owner, 4 + distance);
                    Vector direction = living.getLocation().toVector().subtract(next_block.getLocation().toVector()).setY(0).normalize();
                    VelocityUtil.setVelocity(living, direction,
                            1 + 0.1 * distance, true, 0.6 + 0.05 * distance, 0, 10, true);
                    already_hit.add(hit);
                }
                last_block = next_block;
            }
        }, 0L, 0L);
    }

    private Location getBlockUnderneath(Location loc) {
        if (loc.getBlock().getType().isSolid()) {
            return loc.getBlock().getLocation();
        }
        while (loc.getY() > 0) {
            loc = loc.getBlock().getRelative(BlockFace.DOWN).getLocation();
            if (!loc.getBlock().getType().isSolid()) {
                continue;
            }
            return loc;
        }
        return loc;
    }

    private void stop() {
        Bukkit.getScheduler().cancelTask(task);
    }

}
