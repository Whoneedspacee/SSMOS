package SSM.Projectiles;

import SSM.Abilities.SpinWeb;
import SSM.Utilities.DamageUtil;
import SSM.Utilities.VelocityUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class WebProjectile extends Projectile {

    public WebProjectile(Player firer, String name) {
        super(firer, name);
    }

    public void createWeb(Location location, long ticks_stay) {
        if(projectile.getTicksLived() > 3) {
            Block replace = location.getBlock();
            Material replacedType = replace.getType();
            if (replacedType == Material.WEB) {
                return;
            }
            replace.setType(Material.WEB);
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    replace.setType(replacedType);
                }
            }, ticks_stay);
        }
    }

    @Override
    protected Entity getProjectileEntity() {
        ItemStack cobweb = new ItemStack(Material.WEB, 1);
        return firer.getWorld().dropItem(firer.getLocation().add(0, 0.5, 0), cobweb);
    }

    @Override
    protected void doVelocity() {
        Vector spread = new Vector(Math.random(), Math.random(), Math.random()).subtract(new Vector(0.5, 0.5, 0.5));
        spread.normalize();
        spread.multiply(0.2);
        Vector final_velocity = firer.getLocation().getDirection().multiply(-1).add(spread);
        VelocityUtil.setVelocity(projectile, final_velocity,
                Math.random() * 0.4 + 1, false, 0, 0.2, 10, false);
    }

    @Override
    protected void doEffect() {
        return;
    }

    @Override
    protected boolean onExpire() {
        createWeb(projectile.getLocation(), 40);
        return true;
    }

    @Override
    protected boolean onHitLivingEntity(LivingEntity hit) {
        createWeb(hit.getLocation(), 60);
        DamageUtil.damage(hit, firer, getDamage(), getKnockbackMultiplier(),
                false, EntityDamageEvent.DamageCause.CUSTOM, projectile.getLocation(), false);
        VelocityUtil.setVelocity(hit, new Vector(0, 0, 0));
        return true;
    }

    @Override
    protected boolean onHitBlock(Block hit) {
        createWeb(projectile.getLocation(), 40);
        return true;
    }

    @Override
    protected boolean onIdle() {
        createWeb(projectile.getLocation(), 40);
        return true;
    }

    @Override
    public double getDamage() {
        return 6;
    }

    @Override
    public double getHitboxMultiplier() {
        return 0.5;
    }

    @Override
    public double getKnockbackMultiplier() {
        return 0;
    }

}
