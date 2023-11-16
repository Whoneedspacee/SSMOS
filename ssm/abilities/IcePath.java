package ssm.abilities;

import ssm.gamemanagers.BlockRestoreManager;
import ssm.gamemanagers.ownerevents.OwnerRightClickEvent;
import ssm.utilities.VelocityUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class IcePath extends Ability implements OwnerRightClickEvent {

    private int path_task = -1;
    protected long melt_time_ms = 6000;
    protected List<Block> blocks = new ArrayList<Block>();

    public IcePath() {
        super();
        this.name = "Ice Path";
        this.cooldownTime = 12;
        this.description = new String[]{
                ChatColor.RESET + "Create a temporary icy path in the",
                ChatColor.RESET + "direction you are looking.",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        owner.teleport(owner.getLocation().add(0, 1, 0));
        VelocityUtil.setVelocity(owner, new Vector(0, 0.5, 0));
        blocks.clear();
        //Add Blocks
        if (Math.abs(owner.getLocation().getDirection().getX()) > Math.abs(owner.getLocation().getDirection().getZ())) {
            getBlocks(owner.getLocation().add(0, 0, 1), 16);
            getBlocks(owner.getLocation().add(0, 0, -1), 16);
        } else {
            getBlocks(owner.getLocation().add(1, 0, 0), 16);
            getBlocks(owner.getLocation().add(-1, 0, 0), 16);
        }
        getBlocks(owner.getLocation(), 16);
        //Sort Blocks
        for (int i = 0; i < blocks.size(); i++) {
            for (int j = 0; j + 1 < blocks.size(); j++) {
                if (owner.getLocation().distance(blocks.get(j).getLocation().add(0.5, 0.5, 0.5)) >
                        owner.getLocation().distance(blocks.get(j + 1).getLocation().add(0.5, 0.5, 0.5))) {
                    Block temp = blocks.get(j);
                    blocks.set(j, blocks.get(j + 1));
                    blocks.set(j + 1, temp);
                }
            }
        }
        if(Bukkit.getScheduler().isQueued(path_task) || Bukkit.getScheduler().isCurrentlyRunning(path_task)) {
            Bukkit.getScheduler().cancelTask(path_task);
        }
        path_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (blocks.size() <= 0) {
                    return;
                }
                Block block = blocks.remove(0);
                if (block == null) {
                    return;
                }
                block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, 79);
                BlockRestoreManager.ourInstance.add(block, 79, (byte) 0, melt_time_ms);
            }
        }, 0, 0);
    }

    public void getBlocks(Location loc, int length) {
        //Below Player
        loc.subtract(0, 1, 0);
        Vector dir = loc.getDirection();
        double hLength = Math.sqrt(dir.getX() * dir.getX() + dir.getZ() * dir.getZ());
        if (Math.abs(dir.getY()) > hLength) {
            if (dir.getY() > 0)
                dir.setY(hLength);
            else
                dir.setY(-hLength);

            dir.normalize();
        }
        //Backtrack
        loc.subtract(dir.clone().multiply(2));
        double dist = 0;
        while (dist < length) {
            dist += 0.2;
            loc.add(dir.clone().multiply(0.2));
            if (loc.getBlock().getType() == Material.ICE) {
                continue;
            }
            if (loc.getBlock().getType() == Material.AIR || loc.getBlock().getType() == Material.SNOW) {
                if (!blocks.contains(loc.getBlock())) {
                    blocks.add(loc.getBlock());
                }
            }
        }
    }

}