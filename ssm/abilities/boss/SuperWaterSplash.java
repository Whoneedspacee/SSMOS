package ssm.abilities.boss;

import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import ssm.abilities.Ability;
import ssm.events.SmashDamageEvent;
import ssm.managers.BlockRestoreManager;
import ssm.managers.CooldownManager;
import ssm.managers.ownerevents.OwnerRightClickEvent;
import ssm.managers.ownerevents.OwnerTakeSmashDamageEvent;
import ssm.utilities.BlocksUtil;
import ssm.utilities.DamageUtil;
import ssm.utilities.Utils;
import ssm.utilities.VelocityUtil;

import java.util.Collection;
import java.util.HashMap;

public class SuperWaterSplash extends Ability implements OwnerRightClickEvent, OwnerTakeSmashDamageEvent {

    private int splash_task = -1;
    protected long minimum_air_time_ms = 750;
    protected long second_boost_time_ms = 800;
    protected double first_velocity = 1;
    protected double second_velocity = 1.5;
    protected double pull_in_radius = 15;
    protected double damage_radius = 10;
    protected double damage = 50;
    protected double block_destroy_radius = 8;

    public SuperWaterSplash() {
        super();
        this.name = "Super Water Splash";
        this.cooldownTime = 10;
        this.description = new String[]{
                ChatColor.RESET + "You bounce into the air and pull all nearby players",
                ChatColor.RESET + "towards you.",
                ChatColor.RESET + "Blocking with the sword while bouncing increases the height.",
                ChatColor.RESET + "Landing causes a water splash dealing damage and knockback",
                ChatColor.RESET + "to nearby players and flattening the area around you."
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        if (Bukkit.getScheduler().isQueued(splash_task) || Bukkit.getScheduler().isCurrentlyRunning(splash_task)) {
            return;
        }
        checkAndActivate();
    }

    public void activate() {
        VelocityUtil.setVelocity(owner, new Vector(0, first_velocity, 0));
        for (Player player : Utils.getNearby(owner.getLocation(), pull_in_radius)) {
            if(player.equals(owner)) {
                continue;
            }
            if (!DamageUtil.canDamage(player, owner)) {
                continue;
            }
            Vector trajectory = owner.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            trajectory.setY(0.5);
            VelocityUtil.setVelocity(player, trajectory);
        }
        owner.getWorld().playSound(owner.getLocation(), Sound.AMBIENCE_RAIN, 1.0f, 0.5f);
        splash_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            private boolean boost_used = false;

            @Override
            public void run() {
                if (owner == null) {
                    Bukkit.getScheduler().cancelTask(splash_task);
                    return;
                }
                Utils.playParticle(EnumParticle.DRIP_WATER, owner.getLocation(),
                        0.5f, 0.5f, 0.5f, 0.01f, 10, 96, owner.getWorld().getPlayers());
                long time_elapsed = CooldownManager.getInstance().getTimeElapsedFor(SuperWaterSplash.this, owner);
                if (Utils.entityIsDirectlyOnGround(owner) && owner.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.FENCE &&
                        time_elapsed >= minimum_air_time_ms) {
                    Utils.playParticle(EnumParticle.FIREWORKS_SPARK, owner.getEyeLocation(),
                            0, 0, 0, 0.5f, 50, 96, owner.getWorld().getPlayers());
                    for (Block block : BlocksUtil.getInRadius(owner.getLocation(), damage_radius).keySet()) {
                        if (Math.random() < 0.9 || BlocksUtil.isAirOrFoliage(block) || !BlocksUtil.isAirOrFoliage(block.getRelative(BlockFace.UP))) {
                            continue;
                        }
                        block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType());
                    }
                    owner.getWorld().playSound(owner.getLocation(), Sound.SPLASH2, 2f, 0f);
                    HashMap<LivingEntity, Double> targets = Utils.getInRadius(owner.getLocation(), damage_radius);
                    for (LivingEntity livingEntity : targets.keySet()) {
                        if (!(livingEntity instanceof Player)) {
                            continue;
                        }
                        if (livingEntity.equals(owner) || !DamageUtil.canDamage(livingEntity, owner)) {
                            continue;
                        }
                        double damage_multiplier = targets.get(livingEntity);
                        SmashDamageEvent smashDamageEvent = new SmashDamageEvent(livingEntity, owner, damage * damage_multiplier);
                        smashDamageEvent.setIgnoreDamageDelay(true);
                        smashDamageEvent.setReason(name);
                        smashDamageEvent.callEvent();
                    }
                    Bukkit.getScheduler().cancelTask(splash_task);
                    // Destroy Blocks
                    Collection<Block> blocks = BlocksUtil.getInRadius(owner.getLocation(), block_destroy_radius).keySet();
                    blocks.removeIf(b -> b.getType() == Material.STATIONARY_LAVA || b.getType() == Material.LAVA || b.getType() == Material.BEDROCK);
                    BlockRestoreManager.BlockExplosion(blocks, owner.getLocation(), true, true, 10000L);
                    return;
                } else if (owner.isBlocking() && time_elapsed >= second_boost_time_ms && !boost_used) {
                    boost_used = true;
                    Vector direction = owner.getLocation().getDirection().multiply(second_velocity);
                    VelocityUtil.setVelocity(owner, direction);
                }
            }
        }, 0L, 0L);
    }

    @Override
    public void onOwnerTakeSmashDamageEvent(SmashDamageEvent e) {
        if (Bukkit.getScheduler().isQueued(splash_task) || Bukkit.getScheduler().isCurrentlyRunning(splash_task)) {
            e.multiplyKnockback(0);
        }
    }

}