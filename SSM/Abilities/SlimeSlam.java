package SSM.Abilities;

import SSM.GameManagers.CooldownManager;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Utilities.BlocksUtil;
import SSM.Utilities.DamageUtil;
import SSM.Utilities.Utils;
import SSM.Utilities.VelocityUtil;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class SlimeSlam extends Ability implements OwnerRightClickEvent {

    double damage = 8.0;
    double recoilDamage = 0.5;
    int task = -1;

    public SlimeSlam() {
        this.name = "Slime Slam";
        this.cooldownTime = 6;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        VelocityUtil.setVelocity(owner, owner.getLocation().getDirection(), 1.2, false, 0, 0.2, 1.2, true);
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                List<Entity> possible_hit = owner.getNearbyEntities(2, 2, 2);
                for(Entity entity : possible_hit) {
                    if(!(entity instanceof Player)) {
                        return;
                    }
                    LivingEntity living = (LivingEntity) entity;
                    if(!DamageUtil.canDamage(living, damage)) {
                        continue;
                    }
                    doSlam(living);
                    Bukkit.getScheduler().cancelTask(task);
                    return;
                }
                if(Utils.entityIsOnGround(owner) &&
                        CooldownManager.getInstance().getRemainingTimeFor(SlimeSlam.this, owner) < 5000) {
                    Bukkit.getScheduler().cancelTask(task);
                    return;
                }
            }
        }, 0L, 0L);
    }

    public void doSlam(LivingEntity target) {
        DamageUtil.damage(owner, target, damage/4,
                2, false, EntityDamageEvent.DamageCause.CUSTOM, null);
        DamageUtil.damage(target, owner, damage,
                2, false, EntityDamageEvent.DamageCause.CUSTOM, null);
    }

}
