package SSM.Abilities;

import SSM.Events.SmashDamageEvent;
import SSM.GameManagers.CooldownManager;
import SSM.GameManagers.GameManager;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Utilities.DamageUtil;
import SSM.Utilities.ServerMessageType;
import SSM.Utilities.Utils;
import SSM.Utilities.VelocityUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class SlimeSlam extends Ability implements OwnerRightClickEvent {

    private int task = -1;
    private double damage = 7;
    private double hitbox = 2;

    public SlimeSlam() {
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
                    if (!DamageUtil.canDamage(player, owner, damage)) {
                        continue;
                    }
                    if(owner.getLocation().distance(player.getLocation()) >= hitbox) {
                        continue;
                    }
                    doSlam(player);
                    Bukkit.getScheduler().cancelTask(task);
                    return;
                }
                if(!Utils.entityIsOnGround(owner)) {
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
