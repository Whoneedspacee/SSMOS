package ssm.abilities;

import ssm.events.SmashDamageEvent;
import ssm.managers.CooldownManager;
import ssm.managers.ownerevents.OwnerRightClickEvent;
import ssm.utilities.DamageUtil;
import ssm.utilities.ServerMessageType;
import ssm.utilities.Utils;
import ssm.utilities.VelocityUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class SlimeSlam extends Ability implements OwnerRightClickEvent {

    private int task = -1;
    protected double damage = 7;
    protected double hitbox = 2;

    public SlimeSlam() {
        super();
        this.name = "Slime Slam";
        this.cooldownTime = 6;
        this.usage = AbilityUsage.RIGHT_CLICK;
        this.description = new String[] {
                ChatColor.RESET + "Throw your slimey body forwards. If you hit",
                ChatColor.RESET + "another player before you land, you deal",
                ChatColor.RESET + "large damage and knockback to them.",
                ChatColor.RESET + "",
                ChatColor.RESET + "However, you take 25% of the damage and",
                ChatColor.RESET + "knockback in the opposite direction.",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        VelocityUtil.setVelocity(owner, owner.getLocation().getDirection(), 1.2, false, 0, 0.2, 1.2, true);
        if(Bukkit.getScheduler().isQueued(task) || Bukkit.getScheduler().isCurrentlyRunning(task)) {
            Bukkit.getScheduler().cancelTask(task);
        }
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(owner == null) {
                    Bukkit.getScheduler().cancelTask(task);
                    return;
                }
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.equals(owner)) {
                        continue;
                    }
                    if (!DamageUtil.canDamage(player, owner)) {
                        continue;
                    }
                    if(owner.getLocation().distance(player.getLocation()) >= hitbox) {
                        continue;
                    }
                    doSlam(player);
                    Bukkit.getScheduler().cancelTask(task);
                    return;
                }
                if(!Utils.checkLeapEndGrounded(owner)) {
                    return;
                }
                if(CooldownManager.getInstance().getTimeElapsedFor(SlimeSlam.this, owner) < 1000) {
                    return;
                }
                Bukkit.getScheduler().cancelTask(task);
            }
        }, 0L, 0L);
    }

    public void doSlam(LivingEntity target) {
        SmashDamageEvent recoilEvent = new SmashDamageEvent(owner, target, damage / 4);
        recoilEvent.multiplyKnockback(2);
        recoilEvent.setIgnoreDamageDelay(true);
        recoilEvent.setReason(name + " Recoil");
        recoilEvent.callEvent();
        SmashDamageEvent smashDamageEvent = new SmashDamageEvent(target, owner, damage);
        smashDamageEvent.multiplyKnockback(2);
        smashDamageEvent.setIgnoreDamageDelay(true);
        smashDamageEvent.setReason(name);
        smashDamageEvent.callEvent();
        Utils.sendAttributeMessage("You hit " + ChatColor.YELLOW + target.getName()
                + ChatColor.GRAY + " with", name, owner, ServerMessageType.GAME);
        if(target instanceof Player) {
            Player player = (Player) target;
            Utils.sendAttributeMessage(ChatColor.YELLOW + owner.getName() +
                    ChatColor.GRAY + " hit you with", name, player, ServerMessageType.GAME);
        }
    }

}
