package ssm.abilities;

import ssm.events.SmashDamageEvent;
import ssm.managers.DisguiseManager;
import ssm.managers.disguises.Disguise;
import ssm.managers.disguises.SheepDisguise;
import ssm.managers.ownerevents.OwnerRightClickEvent;
import ssm.utilities.DamageUtil;
import ssm.utilities.LineParticle;
import ssm.utilities.ServerMessageType;
import ssm.utilities.Utils;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class StaticLaser extends Ability implements OwnerRightClickEvent {

    private int laser_task = -1;
    protected double damage = 7;
    protected double hitbox_radius = 1.5;
    protected double damage_radius = 2.5;
    protected double knockback_multiplier = 3;
    protected double range = 40;
    protected float max_charge = 0.99f;
    protected float charge_per_tick = 0.035f;

    public StaticLaser() {
        super();
        this.name = "Static Laser";
        this.cooldownTime = 7;
        this.useMessage = null;
        this.description = new String[] {
                ChatColor.RESET + "Charge up static electricity in your",
                ChatColor.RESET + "wooly coat, and then unleash it upon",
                ChatColor.RESET + "enemies in a devastating laser beam!",

        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        if(Bukkit.getScheduler().isQueued(laser_task) || Bukkit.getScheduler().isCurrentlyRunning(laser_task)) {
            Bukkit.getScheduler().cancelTask(laser_task);
        }
        laser_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(owner == null) {
                    Bukkit.getScheduler().cancelTask(laser_task);
                    return;
                }
                if(!owner.isBlocking()) {
                    Bukkit.getScheduler().cancelTask(laser_task);
                    fireLaser();
                    return;
                }
                owner.setExp(Math.min(max_charge, owner.getExp() + charge_per_tick));
                owner.getWorld().playSound(owner.getLocation(), Sound.FIZZ, 0.25f + owner.getExp(), 0.75f + owner.getExp());
                if(Math.random() > 0.5) {
                    setWoolColor(DyeColor.YELLOW);
                } else {
                    setWoolColor(DyeColor.BLACK);
                }
                if(owner.getExp() >= max_charge) {
                    Bukkit.getScheduler().cancelTask(laser_task);
                    fireLaser();
                    return;
                }
            }
        }, 0L ,0L);
    }

    public void fireLaser() {
        if(owner.getExp() <= 0.2) {
            setWoolColor(DyeColor.WHITE);
            owner.setExp(0f);
            return;
        }
        LineParticle lineParticle = new LineParticle(owner.getEyeLocation(), owner.getLocation().getDirection(),
                0.2f, range * owner.getExp(), EnumParticle.FIREWORKS_SPARK, owner.getWorld().getPlayers());
        Player target = null;
        while (!lineParticle.update() && target == null) {
            for(Player player : Utils.getNearby(lineParticle.getLastLocation().subtract(0, 1, 0), hitbox_radius)) {
                if(player.equals(owner)) {
                    continue;
                }
                target = player;
            }
        }
        Location target_location = lineParticle.getDestination();
        Utils.playParticle(EnumParticle.EXPLOSION_NORMAL, target_location,
                0, 0, 0, 0, 1, 96, target_location.getWorld().getPlayers());
        Utils.playFirework(target_location, FireworkEffect.Type.BURST, Color.YELLOW, false, false);
        for(LivingEntity livingEntity : Utils.getInRadius(target_location, damage_radius).keySet()) {
            if(livingEntity.equals(owner)) {
                continue;
            }
            if(!DamageUtil.canDamage(livingEntity, owner)) {
                continue;
            }
            SmashDamageEvent smashDamageEvent = new SmashDamageEvent(livingEntity, owner, damage * owner.getExp());
            smashDamageEvent.multiplyKnockback(knockback_multiplier);
            smashDamageEvent.setIgnoreDamageDelay(true);
            smashDamageEvent.setReason(name);
            smashDamageEvent.callEvent();
        }
        Utils.sendAttributeMessage("You fired", name, owner, ServerMessageType.SKILL);
        owner.getWorld().playSound(owner.getEyeLocation(), Sound.ZOMBIE_REMEDY, 0.5f + owner.getExp(), 1.75f - owner.getExp());
        owner.getWorld().playSound(owner.getLocation(), Sound.SHEEP_IDLE, 2f, 1.5f);
        setWoolColor(DyeColor.WHITE);
        owner.setExp(0f);
    }

    public void setWoolColor(DyeColor color) {
        Disguise disguise = DisguiseManager.disguises.get(owner);
        if(!(disguise instanceof SheepDisguise)) {
            return;
        }
        SheepDisguise sheepDisguise = (SheepDisguise) disguise;
        sheepDisguise.setColor(color);
    }

}