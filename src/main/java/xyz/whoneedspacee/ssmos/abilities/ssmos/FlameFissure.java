package xyz.whoneedspacee.ssmos.abilities.ssmos;

import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.whoneedspacee.ssmos.managers.ownerevents.OwnerRightClickEvent;
import xyz.whoneedspacee.ssmos.projectiles.ssmos.FlameFissureProjectile;
import xyz.whoneedspacee.ssmos.abilities.Ability;
import xyz.whoneedspacee.ssmos.utilities.ServerMessageType;
import xyz.whoneedspacee.ssmos.utilities.Utils;
import xyz.whoneedspacee.ssmos.attributes.Attribute;

import java.util.ArrayList;
import java.util.List;

public class FlameFissure extends Ability implements OwnerRightClickEvent {

    protected Block target = null;
    protected List<Block> blocks = new ArrayList<Block>();
    protected int fissure_task = -1;
    protected long activation_time_ms;
    protected int projectiles_per_tick = 3;
    protected long duration_ms = 4000;

    public FlameFissure() {
        super();
        this.name = "Flame Fissure";
        this.cooldownTime = 14;
        this.usage = Attribute.AbilityUsage.RIGHT_CLICK;
        this.description = new String[] {
                ChatColor.RESET + "Target a location to create a fissure.",
                ChatColor.RESET + "After a few seconds, the fissure spews fire",
                ChatColor.RESET + "damaging nearby opponents.",
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
        blocks.add(target);
        Bukkit.getScheduler().cancelTask(fissure_task);
        checkAndActivate();
    }

    public void activate() {
        activation_time_ms = System.currentTimeMillis();
        fissure_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            int ticks = 0;

            @Override
            public void run() {
                if(owner == null) {
                    Bukkit.getScheduler().cancelTask(fissure_task);
                    return;
                }
                if(ticks % 3 == 0) {
                    for(Block block : blocks) {
                        Utils.playParticle(EnumParticle.FLAME, block.getLocation().add(0.5, 1, 0.5),
                                0.25f, 0, 0.25f, 0, 5, 96, block.getWorld().getPlayers());
                    }
                }
                for(Block block : blocks) {
                    Utils.playParticle(EnumParticle.FLAME, block.getLocation().add(0.5, 1, 0.5),
                            0.25f, 0, 0.25f, 0.5f, 1, 96, block.getWorld().getPlayers());
                }
                if (System.currentTimeMillis() - activation_time_ms < 1000) {
                    /*for(Block block : blocks) {
                        if(Math.random() < 0.25) {
                            float progress = (float) CooldownManager.getInstance().getTimeElapsedFor(FlameFissure.this, owner) / 1000;
                            block.getWorld().playSound(block.getLocation(), Sound.BLAZE_BREATH, 0.5f, 1f + progress);
                        }
                    }*/
                    return;
                }
                if (System.currentTimeMillis() - activation_time_ms > duration_ms) {
                    Bukkit.getScheduler().cancelTask(fissure_task);
                    return;
                }
                for(int i = 0; i < projectiles_per_tick; i++) {
                    Block random = blocks.get((int) (Math.random() * blocks.size()));
                    FlameFissureProjectile projectile = new FlameFissureProjectile(owner, name, random, ticks * 4.5 * 4);
                    projectile.launchProjectile();
                    if(Math.random() < 0.25) {
                        random.getWorld().playSound(random.getLocation(), Sound.FIZZ, 0.5f, 0.5f);
                    }
                }
                ticks++;
            }
        }, 0L, 0L);
    }

    private boolean isSolid(Block block) {
        return block.getType().isSolid() || block.getType() == Material.SNOW;
    }

}
