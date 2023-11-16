package ssm.projectiles;

import ssm.abilities.CubTackle;
import ssm.events.SmashDamageEvent;
import ssm.utilities.ServerMessageType;
import ssm.utilities.Utils;
import ssm.utilities.VelocityUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class WolfProjectile extends SmashProjectile {

    protected int slow_task = -1;

    public WolfProjectile(Player firer, String name) {
        super(firer, name);
        this.damage = 5;
        this.hitbox_size = 1;
        this.knockback_mult = 0;
        this.expiration_ticks = 70;
        this.blockDetection = false;
    }

    @Override
    public void launchProjectile() {
        super.launchProjectile();
        firer.getWorld().playSound(projectile.getLocation(), Sound.WOLF_BARK, 1f, 1.8f);
    }

    @Override
    public void destroy() {
        if(CubTackle.tackle_wolf.get(firer) != null) {
            CubTackle.tackle_wolf.remove(firer);
        }
        if(CubTackle.tackled_by.get(projectile) != null) {
            CubTackle.tackled_by.remove(projectile);
        }
        super.destroy();
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return (entity instanceof Player);
    }

    @Override
    protected Entity createProjectileEntity() {
        Wolf wolf = (Wolf) firer.getWorld().spawnEntity(firer.getEyeLocation().add(firer.getLocation().getDirection()), EntityType.WOLF);
        wolf.setBaby();
        wolf.setAngry(true);
        wolf.setMaxHealth(30);
        wolf.setHealth(wolf.getMaxHealth());
        CubTackle.tackle_wolf.put(firer, wolf);
        return wolf;
    }

    @Override
    protected void doVelocity() {
        VelocityUtil.setVelocity(projectile, firer.getLocation().getDirection(),
                1.8, false, 0, 0.2, 1.2, true);
    }

    @Override
    protected void doEffect() {
        return;
    }

    @Override
    protected boolean onExpire() {
        // Only cancel before we hit if we are grounded
        if(Utils.entityIsOnGround(projectile)) {
            return true;
        }
        return false;
    }

    @Override
    protected boolean onHitLivingEntity(LivingEntity hit) {
        if(CubTackle.tackled_by.get(projectile) != null) {
            return false;
        }
        if(!(hit instanceof Player)) {
            return true;
        }
        Player player = (Player) hit;
        CubTackle.tackled_by.put((Wolf) projectile, player);
        projectile.setVelocity(new Vector(0, -0.6, 0));
        VelocityUtil.setVelocity(player, new Vector(0, 0, 0));
        SmashDamageEvent smashDamageEvent = new SmashDamageEvent(hit, firer, damage);
        smashDamageEvent.multiplyKnockback(knockback_mult);
        smashDamageEvent.setIgnoreDamageDelay(true);
        smashDamageEvent.setReason(name);
        smashDamageEvent.callEvent();
        player.getWorld().playSound(player.getLocation(), Sound.WOLF_GROWL, 1.5f, 1.5f);
        Utils.sendAttributeMessage("You hit " + ChatColor.YELLOW + player.getName()
                + ChatColor.GRAY + " with", name, firer, ServerMessageType.GAME);
        Utils.sendAttributeMessage(ChatColor.YELLOW + firer.getName() + ChatColor.GRAY +
                " hit you with", name, player, ServerMessageType.GAME);
        // Cancel hit detection
        this.cancel();
        if(smashDamageEvent.isCancelled()) {
            return false;
        }
        slow_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(projectile == null || !projectile.isValid()) {
                    destroy();
                    Bukkit.getScheduler().cancelTask(slow_task);
                    return;
                }
                if(projectile.getTicksLived() > getExpirationTicks()) {
                    destroy();
                    Bukkit.getScheduler().cancelTask(slow_task);
                    return;
                }
                if(CubTackle.tackled_by.get(projectile) != null) {
                    Player hit = CubTackle.tackled_by.get(projectile);
                    if(!hit.isValid()) {
                        destroy();
                        return;
                    }
                    if(projectile.getLocation().distance(hit.getLocation()) < 2.5) {
                        hit.removePotionEffect(PotionEffectType.SLOW);
                        hit.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 18, 1, false, false));
                        VelocityUtil.setVelocity(hit, new Vector(0, -0.3, 0));
                    }
                    Location location = hit.getLocation();
                    Vector difference = location.clone().toVector().subtract(projectile.getLocation().clone().toVector()).setY(0).normalize();
                    location.add(difference);
                    Utils.creatureMove(projectile, location, 1);
                }
            }
        }, 0L, 0L);
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

    @EventHandler
    public void tackleTargetCancel(EntityTargetEvent e) {
        if(projectile == null) {
            return;
        }
        if(!e.getEntity().equals(projectile)) {
            return;
        }
        if(e.getTarget() == null) {
            return;
        }
        if(e.getTarget().equals(firer)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void cancelWolfDamage(SmashDamageEvent e) {
        if(projectile == null) {
            return;
        }
        if(e.getDamageCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }
        if(e.getDamager() != null && e.getDamager().equals(projectile)) {
            e.setCancelled(true);
        }
    }

}
