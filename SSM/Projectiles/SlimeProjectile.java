package SSM.Projectiles;

import SSM.Events.SmashDamageEvent;
import SSM.Utilities.DamageUtil;
import SSM.Utilities.ServerMessageType;
import SSM.Utilities.Utils;
import SSM.Utilities.VelocityUtil;
import net.minecraft.server.v1_8_R3.EntitySlime;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class SlimeProjectile extends SmashProjectile {

    private double charge = 0;
    private int clear_slime_task = -1;

    public SlimeProjectile(Player firer, String name, double charge) {
        super(firer, name);
        this.damage = 3;
        this.hitbox_size = 1;
        this.knockback_mult = 3;
        this.charge = charge;
    }

    @Override
    public void launchProjectile() {
        super.launchProjectile();
        clear_slime_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(projectile == null || !(projectile instanceof Slime)) {
                    Bukkit.getScheduler().cancelTask(clear_slime_task);
                    return;
                }
                Slime slime = (Slime) projectile;
                if (slime.getTicksLived() > 120) {
                    slime.setTicksLived(1);

                    int entity_amount = 6 + 6 * slime.getSize();
                    List<Entity> slimeItems = new ArrayList<Entity>();
                    for (int i = 0; i < entity_amount; i++) {
                        Item item = slime.getLocation().getWorld().dropItem(slime.getLocation(), new ItemStack(Material.SLIME_BALL, 1));
                        double velocity = 0.2 + 0.1 * slime.getSize();
                        item.setVelocity(new Vector((Math.random() - 0.5) * velocity, Math.random(), (Math.random() - 0.5) * velocity));
                        item.setPickupDelay(999999);
                        slimeItems.add(item);
                    }

                    Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                        @Override
                        public void run() {
                            for (Entity ent : slimeItems) {
                                ent.remove();
                            }
                        }
                    }, 15L);

                    if (slime.getSize() <= 1) {
                        slime.remove();
                    }
                    else {
                        slime.setSize(slime.getSize() - 1);
                    }
                }
            }
        }, 0, 20L);
    }

    @Override
    protected Entity createProjectileEntity() {
        return null;
    }

    @Override
    protected void doVelocity() {
        VelocityUtil.setVelocity(projectile, firer.getLocation().getDirection(),
                1 + charge / 2d, false, 0, 0.2, 10, true);
    }

    @Override
    protected void doEffect() {
        return;
    }

    @Override
    protected boolean onExpire() {
        return false;
    }

    @Override
    protected boolean onHitLivingEntity(LivingEntity hit) {
        Slime slime = (Slime) projectile;
        double final_damage = damage + slime.getSize() * 3;
        SmashDamageEvent smashDamageEvent = new SmashDamageEvent(hit, firer, final_damage);
        smashDamageEvent.multiplyKnockback(knockback_mult);
        smashDamageEvent.setIgnoreDamageDelay(true);
        smashDamageEvent.setReason(name);
        smashDamageEvent.callEvent();
        playHitSound();
        // Cancel hit detection
        this.cancel();
        return false;
    }

    @Override
    protected boolean onHitBlock(Block hit) {
        // Cancel hit detection
        this.cancel();
        return false;
    }

    @Override
    protected boolean onIdle() {
        return false;
    }

    @EventHandler
    public void onSlimeDeathSplit(SlimeSplitEvent e) {
        if(e.getEntity().equals(projectile)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSlimeTarget(EntityTargetEvent e) {
        if(e.getEntity() == null || e.getTarget() == null) {
            return;
        }
        if(!e.getEntity().equals(projectile)) {
            return;
        }
        if(e.getTarget().equals(firer)) {
            e.setCancelled(true);
            return;
        }
        if(!(e.getTarget() instanceof LivingEntity)) {
            return;
        }
        if(DamageUtil.canDamage((LivingEntity) e.getTarget(), firer, 0)) {
            return;
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onSmashDamage(SmashDamageEvent e) {
        if(projectile == null || e.getDamager() == null) {
            return;
        }
        if(!e.getDamager().equals(projectile)) {
            return;
        }
        if(e.getDamagee().equals(firer)) {
            e.setCancelled(true);
            return;
        }
        if(!DamageUtil.canDamage(e.getDamagee(), firer, e.getDamage())) {
            e.setCancelled(true);
            return;
        }
        Slime slime = (Slime) e.getDamager();
        e.setDamage(2 * slime.getSize());
        e.multiplyKnockback(2);
        firer.setLevel((int) e.getDamage());
    }

}
