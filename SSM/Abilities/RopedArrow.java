package SSM.Abilities;

import SSM.GameManagers.OwnerEvents.OwnerLeftClickEvent;
import SSM.Utilities.VelocityUtil;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class RopedArrow extends Ability implements OwnerLeftClickEvent {

    private Arrow arrow;
    protected int power = 1;

    public RopedArrow() {
        super();
        this.name = "Roped Arrow";
        this.cooldownTime = 5;
        this.usage = AbilityUsage.LEFT_CLICK;
        this.useMessage = "You fired";
        this.description = new String[] {
                ChatColor.RESET + "Instantly fires an arrow. When it ",
                ChatColor.RESET + "collides with something, you are pulled",
                ChatColor.RESET + "towards it, with great power."};
    }

    public void onOwnerLeftClick(PlayerAnimationEvent e) {
        checkAndActivate();
    }

    public void activate() {
        arrow = owner.launchProjectile(Arrow.class);
        arrow.setCustomName("Roped Arrow");
        arrow.setMetadata("Roped Arrow", new FixedMetadataValue(plugin, 1));
        arrow.setVelocity(owner.getLocation().getDirection().multiply(2.4 * power));
    }

    @EventHandler
    public void pullToArrow(ProjectileHitEvent e) {
        if (!e.getEntity().equals(arrow)) {
            return;
        }
        Vector p = owner.getLocation().toVector();
        Vector a = arrow.getLocation().toVector();
        Vector pre = a.subtract(p);
        Vector trajectory = pre.normalize();
        double mult = (arrow.getVelocity().length() / 3d);

        VelocityUtil.setVelocity(owner, trajectory, 0.4 + mult * power, false,
                0, 0.6 * mult * power, 1.2 * mult * power, true);

        arrow.getWorld().playSound(arrow.getLocation(), Sound.ARROW_HIT, 2.5f, 0.5f);
        arrow = null;
    }

    @EventHandler
    public void arrowDamage(EntityDamageByEntityEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            if (e.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) e.getDamager();
                if (arrow.hasMetadata("Roped Arrow")) {
                    e.setDamage(6.0);
                }
            }
        }
    }
}
