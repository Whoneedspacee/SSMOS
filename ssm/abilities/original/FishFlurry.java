package ssm.abilities.original;

import ssm.abilities.Ability;
import ssm.managers.ownerevents.OwnerRightClickEvent;
import ssm.projectiles.FishFlurryProjectile;
import ssm.utilities.BlocksUtil;
import ssm.utilities.ServerMessageType;
import ssm.utilities.Utils;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class FishFlurry extends Ability implements OwnerRightClickEvent {

    protected Block target = null;
    protected List<Block> blocks = new ArrayList<Block>();
    protected int flurry_task = -1;
    protected long activation_time_ms;

    public FishFlurry() {
        super();
        this.name = "Fish Flurry";
        this.cooldownTime = 16;
        this.usage = AbilityUsage.RIGHT_CLICK;
        this.description = new String[] {
                ChatColor.RESET + "Target a location to create a geyser.",
                ChatColor.RESET + "After a few seconds, the geyser will explode",
                ChatColor.RESET + "with all sorts of marine life which will",
                ChatColor.RESET + "damage nearby opponents.",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        if(!check()) {
            return;
        }
        target = Utils.getTargetBlock(owner, 64);
        if (target == null || target.getType() == Material.AIR) {
            Utils.sendServerMessageToPlayer("You must target a block.",
                    owner, ServerMessageType.SKILL);
            return;
        }
        blocks.clear();
        for (Block cur : BlocksUtil.getInRadius(target.getLocation(), 3.5).keySet()) {
            if (cur == null) {
                continue;
            }
            if (!cur.getType().isSolid() || cur.isLiquid()) {
                continue;
            }
            Block cur_up = cur.getRelative(BlockFace.UP);
            if (cur_up.getType().isSolid()) {
                continue;
            }
            blocks.add(cur);
        }
        if(!blocks.isEmpty()) {
            Bukkit.getScheduler().cancelTask(flurry_task);
            checkAndActivate();
        }
    }

    public void activate() {
        activation_time_ms = System.currentTimeMillis();
        flurry_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            int ticks = 0;

            @Override
            public void run() {
                if(owner == null) {
                    Bukkit.getScheduler().cancelTask(flurry_task);
                    return;
                }
                if(ticks % 3 == 0) {
                    for(Block block : blocks) {
                        Utils.playParticle(EnumParticle.WATER_SPLASH, block.getLocation().add(0.5, 1, 0.5),
                                0.25f, 0, 0.25f, 0, 1, 96, block.getWorld().getPlayers());
                    }
                }
                Block random = blocks.get((int) (Math.random() * blocks.size()));
                if(Math.random() > 0.5) {
                    random.getWorld().playSound(random.getLocation(), Math.random() > 0.5 ? Sound.SPLASH : Sound.SPLASH2, 0.5f, 1f);
                }
                if(System.currentTimeMillis() - activation_time_ms < 1000) {
                    return;
                }
                if(System.currentTimeMillis() - activation_time_ms > 4000) {
                    Bukkit.getScheduler().cancelTask(flurry_task);
                    return;
                }
                FishFlurryProjectile projectile = new FishFlurryProjectile(owner, name, random);
                projectile.launchProjectile();
                ticks++;
            }
        }, 0L, 0L);
    }

}
