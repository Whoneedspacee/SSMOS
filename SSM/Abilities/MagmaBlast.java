package SSM.Abilities;

import SSM.Events.SmashDamageEvent;
import SSM.GameManagers.OwnerEvents.OwnerDealSmashDamageEvent;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Utilities.Utils;
import SSM.Utilities.VelocityUtil;
import net.minecraft.server.v1_8_R3.EntityLargeFireball;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLargeFireball;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;

public class MagmaBlast extends Ability implements OwnerRightClickEvent, OwnerDealSmashDamageEvent {

    private double velocity = 0.2;
    private double velocity_radius = 8;
    private double damage = 8;

    public MagmaBlast() {
        super();
        this.name = "Magma Blast";
        this.cooldownTime = 6;
        this.description = new String[]{
                ChatColor.RESET + "Release a powerful ball of magma which explodes",
                ChatColor.RESET + "on impact, dealing damage and knockback.",
                ChatColor.RESET + "",
                ChatColor.RESET + "You receive strong knockback when you shoot it.",
                ChatColor.RESET + "Use this knockback to get back onto the map!",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        LargeFireball ball = owner.launchProjectile(LargeFireball.class);
        ball.setShooter(owner);
        ball.setIsIncendiary(false);
        ball.setYield(0);
        ball.setBounce(false);
        ball.teleport(owner.getEyeLocation().add(owner.getLocation().getDirection().multiply(1)));
        Vector dir = owner.getLocation().getDirection().multiply(velocity);
        EntityLargeFireball eFireball = ((CraftLargeFireball) ball).getHandle();
        eFireball.dirX = dir.getX();
        eFireball.dirY = dir.getY();
        eFireball.dirZ = dir.getZ();
        VelocityUtil.setVelocity(owner, owner.getLocation().getDirection().multiply(-1), 1.2, false, 0, 0.2, 1.2, true);
        ball.setMetadata("Magma Blast", new FixedMetadataValue(plugin, 1));
        owner.getWorld().playSound(owner.getLocation(), Sound.CREEPER_DEATH, 2f, 1.5f);
    }

    @EventHandler
    public void Collide(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        List<MetadataValue> data = projectile.getMetadata("Magma Blast");
        if (data.size() <= 0) {
            return;
        }
        if (projectile.getShooter() == null) {
            return;
        }
        if (!projectile.getShooter().equals(owner)) {
            return;
        }
        projectile.remove();
        HashMap<LivingEntity, Double> hit_entities = Utils.getInRadius(projectile.getLocation().subtract(0, 1, 0), velocity_radius);
        for (LivingEntity livingEntity : hit_entities.keySet()) {
            if(!(livingEntity instanceof Player)) {
                continue;
            }
            Player player = (Player) livingEntity;
            double range = hit_entities.get(livingEntity);
            if (range > 0.8) {
                range = 1;
            }
            SmashDamageEvent smashDamageEvent = new SmashDamageEvent(player, owner, range * damage);
            smashDamageEvent.multiplyKnockback(0);
            smashDamageEvent.setIgnoreDamageDelay(true);
            smashDamageEvent.setReason(name);
            smashDamageEvent.callEvent();
            Vector difference = player.getEyeLocation().toVector().subtract(projectile.getLocation().add(0, -0.5, 0).toVector());
            difference.normalize();
            VelocityUtil.setVelocity(player, difference, 1 + 2 * range, false, 0, 0.2 + 0.4 * range, 1.2, true);
        }
        Utils.playParticle(EnumParticle.LAVA, projectile.getLocation(),
                0.1f, 0.1f, 0.1f, 0.1f, 50, 96, projectile.getWorld().getPlayers());
    }

    // Change direct hit to not have a sound
    @Override
    public void onOwnerDealSmashDamageEvent(SmashDamageEvent e) {
        if(e.getProjectile() == null) {
            return;
        }
        Projectile projectile = e.getProjectile();
        List<MetadataValue> data = projectile.getMetadata("Magma Blast");
        if (data.size() <= 0) {
            return;
        }
        if (projectile.getShooter() == null) {
            return;
        }
        if (!projectile.getShooter().equals(owner)) {
            return;
        }
        e.setDamageCause(EntityDamageEvent.DamageCause.CUSTOM);
        e.setReason(name);
    }

}