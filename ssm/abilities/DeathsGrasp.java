package ssm.abilities;

import ssm.events.SmashDamageEvent;
import ssm.gamemanagers.CooldownManager;
import ssm.gamemanagers.ownerevents.OwnerLeftClickEvent;
import ssm.Main;
import ssm.utilities.DamageUtil;
import ssm.utilities.ServerMessageType;
import ssm.utilities.Utils;
import ssm.utilities.VelocityUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Iterator;

public class DeathsGrasp extends Ability implements OwnerLeftClickEvent {

    private static HashMap<Player, Long> weakness_end_time = new HashMap<Player, Long>();
    private int grasp_task = -1;
    protected double damage = 2;
    protected long leap_duration_ms = 1000;
    protected long weakness_duration_ms = 1000;

    static {
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                for(Iterator<Player> iterator = weakness_end_time.keySet().iterator(); iterator.hasNext();) {
                    Player player = iterator.next();
                    if(player == null) {
                        iterator.remove();
                        continue;
                    }
                    if(!Utils.entityIsOnGround(player)) {
                        continue;
                    }
                    if(System.currentTimeMillis() < weakness_end_time.get(player)) {
                        continue;
                    }
                    iterator.remove();
                }
            }
        }, 0L, 0L);
    }

    public DeathsGrasp() {
        super();
        this.name = "Deaths Grasp";
        this.usage = AbilityUsage.LEFT_CLICK;
        this.cooldownTime = 12;
        this.description = new String[] {
                ChatColor.RESET + "Leap forwards. If you collide with an ",
                ChatColor.RESET + "opponent, you deal damage, throw them ",
                ChatColor.RESET + "behind you and recharge the ability.",
                ChatColor.RESET + "",
                ChatColor.RESET + "Arrows deal double damage to enemies",
                ChatColor.RESET + "recently hit by Deaths Grasp.",
        };
    }

    public void onOwnerLeftClick(PlayerAnimationEvent e) {
        checkAndActivate();
    }

    public void activate() {
        VelocityUtil.setVelocity(owner, owner.getLocation().getDirection(),
                1.4, false, 0, 0.2, 1.2, true);
        owner.getWorld().playSound(owner.getLocation(), Sound.ZOMBIE_HURT, 1f, 1.4f);
        if(Bukkit.getScheduler().isQueued(grasp_task) || Bukkit.getScheduler().isCurrentlyRunning(grasp_task)) {
            Bukkit.getScheduler().cancelTask(grasp_task);
        }
        grasp_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(owner == null) {
                    Bukkit.getScheduler().cancelTask(grasp_task);
                    return;
                }
                if(Utils.entityIsOnGround(owner)) {
                    if(CooldownManager.getInstance().getTimeElapsedFor(DeathsGrasp.this, owner) >= leap_duration_ms) {
                        Bukkit.getScheduler().cancelTask(grasp_task);
                        return;
                    }
                }
                Player hit = null;
                for(Player player : owner.getWorld().getPlayers()) {
                    if(player.equals(owner)) {
                        continue;
                    }
                    if(!DamageUtil.canDamage(player, owner)) {
                        continue;
                    }
                    if(owner.getLocation().distance(player.getLocation()) < 2) {
                        hit = player;
                        break;
                    }
                }
                if(hit == null) {
                    return;
                }
                SmashDamageEvent smashDamageEvent = new SmashDamageEvent(hit, owner, damage);
                smashDamageEvent.multiplyKnockback(0);
                smashDamageEvent.setIgnoreDamageDelay(true);
                smashDamageEvent.setReason(name);
                smashDamageEvent.callEvent();
                Location owner2d = owner.getLocation().clone();
                owner2d.setY(0);
                Location hit2d = hit.getLocation().clone();
                hit2d.setY(0);
                Vector trajectory2d = owner2d.toVector().subtract(hit2d.toVector());
                trajectory2d.normalize();
                VelocityUtil.setVelocity(hit, trajectory2d, 1.6, false, 0, 1.2, 1.8, true);
                VelocityUtil.setVelocity(owner, new Vector(0, 0, 0));
                owner.getWorld().playSound(owner.getLocation(), Sound.ZOMBIE_HURT, 1f, 0.7f);
                weakness_end_time.put(hit, System.currentTimeMillis() + weakness_duration_ms);
                Utils.sendAttributeMessage("You hit " + ChatColor.YELLOW + hit.getName()
                        + ChatColor.GRAY + " with", name, owner, ServerMessageType.GAME);
                Utils.sendAttributeMessage(ChatColor.YELLOW + owner.getName()
                        + ChatColor.GRAY + " hit you with", name, hit, ServerMessageType.GAME);
                DeathsGrasp.this.applyCooldown(2);
                Bukkit.getScheduler().cancelTask(grasp_task);
            }
        }, 0L, 0L);
    }

    // Do this after damage since order matters for damage multiplication
    @EventHandler(priority = EventPriority.HIGH)
    public void multiplyArrowDamage(SmashDamageEvent e) {
        if(owner == null) {
            return;
        }
        if(!(e.getDamager() instanceof Player)) {
            return;
        }
        if(!e.getDamager().equals(owner)) {
            return;
        }
        if(!(e.getProjectile() instanceof Arrow)) {
            return;
        }
        if(!(e.getDamagee() instanceof Player)) {
            return;
        }
        Player player = (Player) e.getDamagee();
        if(!weakness_end_time.containsKey(player)) {
            return;
        }
        e.setDamage(e.getDamage() * 2);
        Utils.playParticle(EnumParticle.REDSTONE, player.getLocation(),
                0.5f, 0.5f, 0.5f, 0, 20, 96, player.getWorld().getPlayers());
        Utils.playParticle(EnumParticle.EXPLOSION_LARGE, player.getLocation(),
                0, 0, 0, 0, 1, 96, player.getWorld().getPlayers());
        owner.getWorld().playSound(owner.getLocation(), Sound.ZOMBIE_HURT, 1f, 2f);
    }

}