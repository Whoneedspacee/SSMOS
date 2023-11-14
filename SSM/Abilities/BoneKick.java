package SSM.Abilities;

import SSM.Events.SmashDamageEvent;
import SSM.GameManagers.CooldownManager;
import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.SkeletonHorseDisguise;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Projectiles.IronHookProjectile;
import SSM.Utilities.DamageUtil;
import SSM.Utilities.ServerMessageType;
import SSM.Utilities.Utils;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class BoneKick extends Ability implements OwnerRightClickEvent {

    private int end_kick_task = -1;
    protected double kick_animation_duration_ms = 1000;
    protected int slow_ticks = 16;
    protected double damage = 6.5;
    protected double knockback_multiplier = 4;
    protected double range = 2.5;

    public BoneKick() {
        super();
        this.name = "Bone Kick";
        this.cooldownTime = 6;
        this.description = new String[]{
                ChatColor.RESET + "Stand on your hind legs and maul enemies",
                ChatColor.RESET + "in front of you with your front legs, ",
                ChatColor.RESET + "dealing damage and large knockback.",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        if(DisguiseManager.disguises.get(owner) instanceof SkeletonHorseDisguise) {
            SkeletonHorseDisguise disguise = (SkeletonHorseDisguise) DisguiseManager.disguises.get(owner);
            disguise.setRearing(true);
        }
        Location location = owner.getLocation();
        location.add(owner.getLocation().getDirection().setY(0).normalize().multiply(1.5));
        location.add(0, 0.8, 0);
        HashMap<LivingEntity, Double> targets = Utils.getInRadius(location, range);
        for(LivingEntity livingEntity : targets.keySet()) {
            if(livingEntity.equals(owner)) {
                continue;
            }
            if(!DamageUtil.canDamage(livingEntity, owner, damage)) {
                continue;
            }
            SmashDamageEvent smashDamageEvent = new SmashDamageEvent(livingEntity, owner, damage);
            smashDamageEvent.multiplyKnockback(knockback_multiplier);
            smashDamageEvent.setIgnoreDamageDelay(true);
            smashDamageEvent.setReason(name);
            smashDamageEvent.callEvent();
            owner.getWorld().playSound(owner.getLocation(), Sound.SKELETON_HURT, 4f, 0.6f);
            owner.getWorld().playSound(owner.getLocation(), Sound.SKELETON_HURT, 4f, 0.6f);
            if(livingEntity instanceof Player) {
                Player player = (Player) livingEntity;
                Utils.sendAttributeMessage(ChatColor.YELLOW + owner.getName() +
                        ChatColor.GRAY + " hit you with", name, player, ServerMessageType.SKILL);
            }
        }
        owner.removePotionEffect(PotionEffectType.SLOW);
        owner.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, slow_ticks, 3, false, false));
        owner.setVelocity(new Vector(0, 0, 0));
        if(Bukkit.getScheduler().isQueued(end_kick_task) || Bukkit.getScheduler().isCurrentlyRunning(end_kick_task)) {
            Bukkit.getScheduler().cancelTask(end_kick_task);
        }
        end_kick_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(owner == null) {
                    Bukkit.getScheduler().cancelTask(end_kick_task);
                    return;
                }
                if(CooldownManager.getInstance().getTimeElapsedFor(BoneKick.this, owner) >= kick_animation_duration_ms) {
                    Bukkit.getScheduler().cancelTask(end_kick_task);
                    if(DisguiseManager.disguises.get(owner) instanceof SkeletonHorseDisguise) {
                        SkeletonHorseDisguise disguise = (SkeletonHorseDisguise) DisguiseManager.disguises.get(owner);
                        disguise.setRearing(false);
                    }
                }
                else {
                    Location location = owner.getLocation();
                    location.add(owner.getLocation().getDirection().setY(0).normalize().multiply(1.5));
                    location.add(0, 0.8, 0);
                    Utils.playParticle(EnumParticle.SMOKE_LARGE, location,
                            0.3f, 0.3f, 0.3f, 0, 2, 96, owner.getWorld().getPlayers());
                }
            }
        }, 0L, 0L);
    }

}