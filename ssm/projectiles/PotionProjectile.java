package ssm.projectiles;

import ssm.events.SmashDamageEvent;
import ssm.gamemanagers.GameManager;
import ssm.utilities.DamageUtil;
import ssm.utilities.Utils;
import ssm.utilities.VelocityUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class PotionProjectile extends SmashProjectile {

    protected double splash_range = 2;
    protected double splash_damage = 5;

    public PotionProjectile(Player firer, String name) {
        super(firer, name);
        this.damage = 7;
        this.hitbox_size = 0;
        this.knockback_mult = 2;
        this.expiration_ticks = 200;
        this.entityDetection = false;
        this.blockDetection = false;
        this.idleDetection = false;
    }

    @Override
    public void run() {
        // We only call a hit when the splash potion splashes
        if (projectile == null || !projectile.isValid()) {
            onHitLivingEntity(null);
            destroy();
            return;
        }
        super.run();
    }

    @Override
    protected Entity createProjectileEntity() {
        ThrownPotion potion = firer.launchProjectile(ThrownPotion.class);
        return potion;
    }

    @Override
    protected void doVelocity() {
        VelocityUtil.setVelocity(projectile, firer.getLocation().getDirection(),
                1, false, 0, 0.2, 10, false);
    }

    @Override
    protected void doEffect() {
        Utils.playParticle(EnumParticle.SPELL_MOB, projectile.getLocation(),
                0.0f, 0.0f, 0.0f, 0.0f, 1, 96, projectile.getWorld().getPlayers());
    }

    @Override
    protected boolean onExpire() {
        return false;
    }

    @Override
    protected boolean onHitLivingEntity(LivingEntity hit) {
        // Don't hit twice
        if(!running) {
            return false;
        }
        List<Player> players = new ArrayList<>(GameManager.getPlayers());
        List<Player> directHit = Utils.getEntitiesInsideEntity(projectile, players);
        for(Player player : directHit) {
            if(player.equals(firer)) {
                continue;
            }
            if(!DamageUtil.canDamage(player, firer)) {
                continue;
            }
            SmashDamageEvent smashDamageEvent = new SmashDamageEvent(player, firer, damage);
            smashDamageEvent.multiplyKnockback(knockback_mult);
            smashDamageEvent.setIgnoreDamageDelay(true);
            smashDamageEvent.setReason(name);
            smashDamageEvent.callEvent();
            player.removePotionEffect(PotionEffectType.SLOW);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 1, false, false));
        }
        players.removeAll(directHit);
        Vector a = projectile.getLocation().subtract(splash_range, splash_range, splash_range).toVector();
        Vector b = projectile.getLocation().add(splash_range, splash_range, splash_range).toVector();
        for(Player player : players) {
            if(player.equals(firer)) {
                continue;
            }
            if(!DamageUtil.canDamage(player, firer)) {
                continue;
            }
            if(!Utils.isInsideBoundingBox(player, a, b)) {
                continue;
            }
            SmashDamageEvent smashDamageEvent = new SmashDamageEvent(player, firer, splash_damage);
            smashDamageEvent.multiplyKnockback(knockback_mult);
            smashDamageEvent.setIgnoreDamageDelay(true);
            smashDamageEvent.setReason(name);
            smashDamageEvent.callEvent();
            player.removePotionEffect(PotionEffectType.SLOW);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 0, false, false));
        }
        return false;
    }

    @Override
    protected boolean onHitBlock(Block hit) {
        return false;
    }

    @Override
    protected boolean onIdle() {
        return false;
    }

}
