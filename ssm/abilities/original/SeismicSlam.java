package ssm.abilities.original;

import ssm.abilities.Ability;
import ssm.events.SmashDamageEvent;
import ssm.managers.ownerevents.OwnerRightClickEvent;
import ssm.utilities.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class SeismicSlam extends Ability implements OwnerRightClickEvent {

    private int task = -1;
    protected double baseDamage = 10;
    protected double range = 8;

    public SeismicSlam() {
        super();
        this.name = "Seismic Slam";
        this.cooldownTime = 10;
        this.description = new String[] {
                ChatColor.RESET + "Begin an earthquake that will give damage",
                ChatColor.RESET + "and knockback to any player who is touching",
                ChatColor.RESET + "the ground, anywhere on the map!",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        Location loc = owner.getLocation();
        Vector direction = loc.getDirection();
        direction.setY(Math.abs(direction.getY()));

        VelocityUtil.setVelocity(owner, direction, 1, true, 1, 0, 1, true);

        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (owner == null || owner.isDead()) {
                    Bukkit.getScheduler().cancelTask(task);
                }
                if (Utils.entityIsDirectlyOnGround(owner)) {
                    Bukkit.getScheduler().cancelTask(task);
                    doSlam();
                }
            }
        }, 20L, 0L);
    }

    public void doSlam() {
        HashMap<LivingEntity, Double> canHit = Utils.getInRadius(owner.getLocation(), range);
        canHit.remove(owner);
        for (LivingEntity other : canHit.keySet()) {
            double damage = baseDamage * canHit.get(other) + 0.5;
            if ((other instanceof Player)) {
                Player hit = (Player) other;
                if (!DamageUtil.canDamage(hit, owner)) {
                    continue;
                }
                Utils.sendAttributeMessage(ChatColor.YELLOW + owner.getName() +
                        ChatColor.GRAY + " hit you with", name, hit, ServerMessageType.GAME);
            }
            SmashDamageEvent smashDamageEvent = new SmashDamageEvent(other, owner, damage);
            smashDamageEvent.multiplyKnockback(2.4);
            smashDamageEvent.setReason(name);
            smashDamageEvent.callEvent();
        }
        owner.getWorld().playSound(owner.getLocation(), Sound.ZOMBIE_WOOD, 2f, 0.2f);
        for (Block check : BlocksUtil.getBlocks(owner.getLocation(), 4)) {
            if (check.getType().isSolid() && !check.getRelative(BlockFace.UP).getType().isSolid()) {
                if(Math.random() < 0.9) {
                    continue;
                }
                check.getWorld().playEffect(check.getLocation(), Effect.STEP_SOUND, check.getType());
            }
        }
    }

}