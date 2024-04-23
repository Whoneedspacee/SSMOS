package xyz.whoneedspacee.ssmos.abilities.original;

import org.bukkit.*;
import xyz.whoneedspacee.ssmos.managers.disguises.Disguise;
import xyz.whoneedspacee.ssmos.managers.disguises.SheepDisguise;
import xyz.whoneedspacee.ssmos.managers.ownerevents.OwnerRightClickEvent;
import xyz.whoneedspacee.ssmos.projectiles.original.WoolProjectile;
import xyz.whoneedspacee.ssmos.abilities.Ability;
import xyz.whoneedspacee.ssmos.events.RechargeAttributeEvent;
import xyz.whoneedspacee.ssmos.events.SmashDamageEvent;
import xyz.whoneedspacee.ssmos.managers.DisguiseManager;
import xyz.whoneedspacee.ssmos.utilities.DamageUtil;
import xyz.whoneedspacee.ssmos.utilities.ServerMessageType;
import xyz.whoneedspacee.ssmos.utilities.Utils;
import xyz.whoneedspacee.ssmos.utilities.VelocityUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class WoolMine extends Ability implements OwnerRightClickEvent {

    protected double after_detonate_cooldown = 8;
    protected double explosion_damage = 14;
    protected double damage_radius = 9;
    protected double knockback_multiplier = 2;
    protected long delay_ms = 800;
    protected long auto_detonate_delay_ms = 8000;
    public WoolProjectile projectile = null;
    public Block wool_block = null;
    public Material block_material = null;
    public byte block_data = 0;
    public long last_delay_time = 0;
    public long arm_time = 0;

    public WoolMine() {
        super();
        this.name = "Wool Mine";
        this.useMessage = null;
        this.cooldownTime = 0;
        this.description = new String[] {
                ChatColor.RESET + "Shear yourself and use the wool as",
                ChatColor.RESET + "an explosive device. You can Right-Click",
                ChatColor.RESET + "a second time to solidify the bomb, and",
                ChatColor.RESET + "a third time to detonate it on command.",
        };
        this.runTaskTimer(plugin, 0L, 5L);
    }

    @Override
    public void run() {
        if(wool_block == null) {
            return;
        }
        if(owner == null) {
            detonate(false);
            cancel();
            return;
        }
        if(System.currentTimeMillis() - arm_time >= auto_detonate_delay_ms) {
            detonate(false);
        } else if(System.currentTimeMillis() - arm_time >= delay_ms) {
            if(wool_block.getData() == 14) {
                wool_block.setData((byte) 0);
            } else {
                wool_block.setData((byte) 14);
            }
        }
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        if(System.currentTimeMillis() - last_delay_time < delay_ms) {
            return;
        }
        if(projectile != null) {
            projectile.solidify(true);
            return;
        }
        if(wool_block != null) {
            detonate(true);
            return;
        }
        checkAndActivate();
    }

    public void activate() {
        projectile = new WoolProjectile(owner, name);
        projectile.launchProjectile();
        setSheared(true);
        last_delay_time = System.currentTimeMillis();
        Utils.sendAttributeMessage("You launched", name, owner, ServerMessageType.SKILL);
    }

    public void detonate(boolean inform) {
        if(wool_block == null) {
            return;
        }
        Utils.playParticle(EnumParticle.EXPLOSION_HUGE, wool_block.getLocation().add(0.5, 0.5, 0.5),
                0, 0, 0, 0, 1, 96, wool_block.getWorld().getPlayers());
        wool_block.getWorld().playSound(wool_block.getLocation(), Sound.EXPLODE, 3f, 0.8f);
        if(owner == null) {
            wool_block.setType(block_material);
            wool_block.setData(block_data);
            wool_block = null;
            return;
        }
        HashMap<LivingEntity, Double> targets = Utils.getInRadius(wool_block.getLocation().add(0.5, 0.5, 0.5), damage_radius);
        for(LivingEntity livingEntity : targets.keySet()) {
            if(!DamageUtil.canDamage(livingEntity, owner)) {
                continue;
            }
            SmashDamageEvent smashDamageEvent = new SmashDamageEvent(livingEntity, owner, explosion_damage * targets.get(livingEntity) + 0.5);
            smashDamageEvent.multiplyKnockback(0);
            smashDamageEvent.setIgnoreDamageDelay(true);
            smashDamageEvent.setReason(name);
            smashDamageEvent.callEvent();
            if(owner == null) {
                wool_block.setType(block_material);
                wool_block.setData(block_data);
                wool_block = null;
                return;
            }
            Vector trajectory = livingEntity.getLocation().toVector().subtract(wool_block.getLocation().toVector().add(new Vector(0.5, 0.5, 0.5)));
            trajectory.setY(0).normalize();
            VelocityUtil.setVelocity(livingEntity, trajectory, 0.5 + 2.5 * targets.get(livingEntity), true, 0.8, 0, 10, true);
            if(livingEntity instanceof Player) {
                Player player = (Player) livingEntity;
                Utils.sendAttributeMessage(ChatColor.YELLOW + owner.getName() +
                        ChatColor.GRAY + " hit you with", name, player, ServerMessageType.GAME);
            }
        }
        wool_block.setType(block_material);
        wool_block.setData(block_data);
        wool_block = null;
        if(inform) {
            Utils.sendAttributeMessage("You detonated", name, owner, ServerMessageType.SKILL);
        }
        applyCooldown(after_detonate_cooldown);
    }

    @EventHandler
    public void onMineRecharge(RechargeAttributeEvent e) {
        if(!e.getAttribute().equals(this)) {
            return;
        }
        setSheared(false);
    }

    public void setSheared(boolean set) {
        Disguise disguise = DisguiseManager.disguises.get(owner);
        if(!(disguise instanceof SheepDisguise)) {
            return;
        }
        SheepDisguise sheepDisguise = (SheepDisguise) disguise;
        if(set) {
            sheepDisguise.setSheared();
        } else if (sheepDisguise.getSheared()) {
            sheepDisguise.setColor(DyeColor.WHITE);
        }
    }

}