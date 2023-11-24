package ssm.attributes.bowcharge;

import ssm.managers.KitManager;
import ssm.kits.original.KitSkeleton;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import java.util.List;

public class Barrage extends BowCharge {

    private Arrow firedArrow;

    public Barrage(double delay, double rate, int maxCharge) {
        super(delay, rate, maxCharge);
        this.name = "Barrage";
        this.usage = AbilityUsage.CHARGE_BOW;
        this.description = 	new String[] {
                        ChatColor.RESET + "Slowly load more arrows into your bow.",
                        ChatColor.RESET + "When you release, you will quickly fire",
                        ChatColor.RESET + "all the arrows in succession."};
    }

    public void firedBow(Arrow arrow) {
        firedArrow = arrow;
        checkAndActivate();
    }

    public void activate() {
        for (int i = 0; i < charge; i++) {
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    Vector spread = new Vector((Math.random() - 0.5) / 10, (Math.random() - 0.5) / 10, (Math.random() - 0.5) / 10);
                    Arrow arrow = owner.launchProjectile(Arrow.class);
                    arrow.setVelocity(owner.getLocation().getDirection().add(spread).multiply(3));
                    arrow.setMetadata("Barrage Arrow", new FixedMetadataValue(plugin, 1));
                    owner.playSound(owner.getLocation(), Sound.SHOOT_ARROW, 1.0F, 1.0F);
                }
            }, (i + 1));
        }
    }

    @EventHandler
    public void clearArrow(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) e.getEntity();
            List<MetadataValue> data = arrow.getMetadata("Barrage Arrow");
            if (data.size() > 0) {
                arrow.remove();
            }
        }
    }

    @EventHandler
    public void arrowDamage(EntityDamageByEntityEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            if (e.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) e.getDamager();
                List<MetadataValue> data = arrow.getMetadata("Barrage Damage");
                if (data.size() > 0) {
                    int damage = data.get(0).asInt();
                    e.setDamage(damage);
                } else if (arrow.getShooter() instanceof Player) {
                    Player player = (Player) arrow.getShooter();
                    if (KitManager.getPlayerKit(player) instanceof KitSkeleton) {
                        e.setDamage(6.0);
                    }
                }
            }
        }
    }

}

