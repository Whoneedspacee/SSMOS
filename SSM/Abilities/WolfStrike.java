package SSM.Abilities;

import SSM.Events.SmashDamageEvent;
import SSM.GameManagers.CooldownManager;
import SSM.GameManagers.GameManager;
import SSM.GameManagers.OwnerEvents.OwnerDealSmashDamageEvent;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Utilities.DamageUtil;
import SSM.Utilities.ServerMessageType;
import SSM.Utilities.Utils;
import SSM.Utilities.VelocityUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class WolfStrike extends Ability implements OwnerRightClickEvent, OwnerDealSmashDamageEvent {

    private int strike_task = -1;
    protected double strike_damage = 7;
    protected long strike_duration_ms = 1500;

    public WolfStrike() {
        super();
        this.name = "Wolf Strike";
        this.cooldownTime = 8;
        this.description = new String[]{
                ChatColor.RESET + "Leap forward with great power.",
                ChatColor.RESET + "If you collide with an enemy, you deal",
                ChatColor.RESET + "damage to them. If they are being tackled",
                ChatColor.RESET + "by a cub, it deals 300% Knockback.",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        VelocityUtil.setVelocity(owner, owner.getLocation().getDirection(), 1.6, false, 1, 0.2, 1.2, true);
        owner.getWorld().playSound(owner.getLocation(), Sound.WOLF_BARK, 1f, 1.2f);
        if (Bukkit.getScheduler().isQueued(strike_task) || Bukkit.getScheduler().isCurrentlyRunning(strike_task)) {
            Bukkit.getScheduler().cancelTask(strike_task);
        }
        strike_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (owner == null) {
                    Bukkit.getScheduler().cancelTask(strike_task);
                }
                for (Player player : owner.getWorld().getPlayers()) {
                    if(player.equals(owner)) {
                        continue;
                    }
                    if(!DamageUtil.canDamage(player, owner, strike_damage)) {
                        continue;
                    }
                    if (owner.getLocation().distance(player.getLocation()) < 1 ||
                            owner.getLocation().distance(player.getEyeLocation()) < 1) {
                        doStrike(player);
                        Bukkit.getScheduler().cancelTask(strike_task);
                        return;
                    }
                }
                if (!Utils.entityIsOnGround(owner)) {
                    return;
                }
                if(CooldownManager.getInstance().getTimeElapsedFor(WolfStrike.this, owner) < strike_duration_ms) {
                    return;
                }
                Bukkit.getScheduler().cancelTask(strike_task);
            }
        }, 0L, 0L);
    }

    public void doStrike(Player hit) {
        VelocityUtil.setVelocity(owner, new Vector(0, 0, 0));
        SmashDamageEvent smashDamageEvent = new SmashDamageEvent(hit, owner, strike_damage);
        smashDamageEvent.setIgnoreDamageDelay(true);
        smashDamageEvent.setReason(name);
        smashDamageEvent.callEvent();
        owner.getWorld().playSound(owner.getLocation(), Sound.WOLF_BARK, 1.5f, 1f);
        Utils.sendAttributeMessage("You hit " + ChatColor.YELLOW + hit.getName() +
                ChatColor.GRAY + " with", name, owner, ServerMessageType.GAME);
        Utils.sendAttributeMessage(ChatColor.YELLOW + owner.getName() +
                ChatColor.GRAY + " hit you with", name, hit, ServerMessageType.GAME);
    }


    @Override
    public void onOwnerDealSmashDamageEvent(SmashDamageEvent e) {
        if(!e.getReason().equals(name)) {
            return;
        }
        if(CubTackle.tackle_wolf.get(owner) != null) {
            Wolf wolf = CubTackle.tackle_wolf.get(owner);
            if(CubTackle.tackled_by.get(wolf).equals(e.getDamagee())) {
                CubTackle.tackled_by.remove(wolf);
                CubTackle.tackle_wolf.remove(owner);
                wolf.remove();
                e.multiplyKnockback(3.0);
                owner.getWorld().playEffect(owner.getLocation(), Effect.STEP_SOUND, 55);
                owner.getWorld().playSound(owner.getLocation(), Sound.WOLF_BARK, 2f, 1.5f);
                return;
            }
        }
        e.multiplyKnockback(1.5);
    }

}
