package SSM.Attributes;

import SSM.Attribute;
import SSM.GameManagers.KitManager;
import SSM.Kit;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Ravage extends Attribute {

    private double damageCap;
    private double damageIncrement;
    private double combo = 0;
    private double expireTime;
    private int newtask;

    public Ravage(double damageCap, double damageIncrement, double expireTime) {
        super();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.damageCap = damageCap;
        this.damageIncrement = damageIncrement;
        this.expireTime = expireTime;
        this.name = "Ravage";
        task = this.runTaskTimer(plugin, 0, 1L);
    }

    @EventHandler
    public void ravage(EntityDamageByEntityEvent e) {
        if (e.getDamager() != owner) {
            return;
        }
        Kit kit = KitManager.getPlayerKit(owner);
        if (!(kit.getMelee() + damageIncrement > damageCap)) {
            kit.setMelee(kit.getMelee() + damageIncrement);
            combo++;
        }
        Bukkit.getScheduler().cancelTask(newtask);
        newtask = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                kit.setMelee(kit.getMelee() - (damageIncrement * combo));
                combo = 0;
            }
        }, (long) (expireTime * 20));

    }

}
