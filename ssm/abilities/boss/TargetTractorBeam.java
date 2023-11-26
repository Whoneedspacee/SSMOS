package ssm.abilities.boss;

import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import ssm.abilities.Ability;
import ssm.events.SmashDamageEvent;
import ssm.managers.CooldownManager;
import ssm.managers.DisguiseManager;
import ssm.managers.disguises.Disguise;
import ssm.managers.disguises.GuardianDisguise;
import ssm.managers.ownerevents.OwnerDealSmashDamageEvent;
import ssm.managers.ownerevents.OwnerKillEvent;
import ssm.managers.ownerevents.OwnerRightClickEvent;
import ssm.utilities.DamageUtil;
import ssm.utilities.ServerMessageType;
import ssm.utilities.Utils;
import ssm.utilities.VelocityUtil;

import java.util.ArrayList;
import java.util.List;

public class TargetTractorBeam extends Ability implements OwnerRightClickEvent, OwnerDealSmashDamageEvent, OwnerKillEvent {

    private int laser_task = -1;
    protected long max_time_ms = 4000;
    protected double max_range = 10;
    protected double pull_velocity = 0.5;
    protected double damage_increase = 2;
    protected double knockback_increase_multiplier = 2;
    List<Player> selected_targets = new ArrayList<Player>();

    public TargetTractorBeam() {
        super();
        this.name = "Target Tractor Beam";
        this.cooldownTime = 12;
        this.description = new String[] {
                ChatColor.RESET + "You target the nearest player with your laser.",
                ChatColor.RESET + "That player gets sucked in towards you.",
                ChatColor.RESET + "Your laser breaks if you get too far away or after some time."
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        if (!check()) {
            return;
        }
        if(selected_targets.size() > 0) {
            return;
        }
        selected_targets = Utils.getNearby(owner.getLocation(), max_range + 2);
        selected_targets.remove(owner);
        if(selected_targets.size() <= 0) {
            Utils.sendServerMessageToPlayer("There are no targets within range.", owner, ServerMessageType.SKILL);
            return;
        }
        checkAndActivate();
    }

    public void activate() {
        recalculateDisguiseTarget();
        for(Player player : selected_targets) {
            Utils.sendAttributeMessage(ChatColor.YELLOW + owner.getName() +
                    ChatColor.GRAY + " targeted you with their", name, player, ServerMessageType.GAME);
        }
        laser_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(owner == null || selected_targets.size() <= 0) {
                    selected_targets.clear();
                    recalculateDisguiseTarget();
                    Bukkit.getScheduler().cancelTask(laser_task);
                    return;
                }
                long time_elapsed = CooldownManager.getInstance().getTimeElapsedFor(TargetTractorBeam.this, owner);
                if(time_elapsed >= max_time_ms) {
                    selected_targets.clear();
                    recalculateDisguiseTarget();
                    Bukkit.getScheduler().cancelTask(laser_task);
                    return;
                }
                for(Player target : selected_targets) {
                    Utils.playParticle(EnumParticle.CRIT_MAGIC, target.getLocation().add(0, 0.5, 0.5),
                            1f, 0.5f, 1f, 0.1f, 10, 96, target.getWorld().getPlayers());
                    target.getWorld().playEffect(target.getLocation().add(0, 0.5, 0), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
                    VelocityUtil.setVelocity(target, owner.getLocation().subtract(target.getLocation()).toVector().normalize().multiply(pull_velocity));
                }
            }
        }, 0L, 5L);
    }

    @Override
    public void onOwnerDealSmashDamageEvent(SmashDamageEvent e) {
        if(selected_targets.size() <= 0) {
            return;
        }
        if(e.getDamagee() == null) {
            return;
        }
        if(!(e.getDamagee() instanceof Player)) {
            return;
        }
        if(!selected_targets.contains((Player) e.getDamagee())) {
            return;
        }
        e.setDamage(e.getDamage() + damage_increase);
        e.multiplyKnockback(knockback_increase_multiplier);
        e.getDamagee().getWorld().playEffect(e.getDamagee().getLocation().add(0, 0.5, 0), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
        selected_targets.remove((Player) e.getDamagee());
        recalculateDisguiseTarget();
    }

    @Override
    public void onOwnerKillEvent(Player player) {
        if(selected_targets.size() <= 0 || !selected_targets.contains(player)) {
            return;
        }
        selected_targets.remove(player);
        recalculateDisguiseTarget();
    }

    public void recalculateDisguiseTarget() {
        if(selected_targets.size() <= 0) {
            disguiseTarget(null);
            return;
        }
        disguiseTarget(selected_targets.get(0));
    }

    public void disguiseTarget(Entity entity) {
        Disguise disguise = DisguiseManager.disguises.get(owner);
        if(!(disguise instanceof GuardianDisguise)) {
            return;
        }
        GuardianDisguise guardianDisguise = (GuardianDisguise) disguise;
        guardianDisguise.setTarget(entity);
    }

}