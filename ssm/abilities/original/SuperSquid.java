package ssm.abilities.original;

import ssm.abilities.Ability;
import ssm.events.SmashDamageEvent;
import ssm.managers.KitManager;
import ssm.managers.ownerevents.OwnerRightClickEvent;
import ssm.utilities.Utils;
import ssm.utilities.VelocityUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SuperSquid extends Ability implements OwnerRightClickEvent {

    private int task = -1;
    private boolean active = false;

    public SuperSquid() {
        super();
        this.name = "Super Squid";
        this.cooldownTime = 8;
        this.usage = AbilityUsage.BLOCKING;
        this.description = new String[]{
                ChatColor.RESET + "You become invulnerable and fly through",
                ChatColor.RESET + "the sky in the direction you are looking.",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            private int ticks = 0;

            @Override
            public void run() {
                if (KitManager.getPlayerKit(owner) == null) {
                    Bukkit.getScheduler().cancelTask(task);
                    active = false;
                    return;
                }
                if (!owner.isBlocking() || ticks >= 22) {
                    Bukkit.getScheduler().cancelTask(task);
                    active = false;
                    KitManager.getPlayerKit(owner).setInvincible(false);
                    return;
                }
                active = true;
                KitManager.getPlayerKit(owner).setInvincible(true);
                VelocityUtil.setVelocity(owner, 0.6, 0.1, 1, true);
                owner.getWorld().playSound(owner.getLocation(), Sound.SPLASH2, 0.5f, 1f);
                Utils.playParticle(EnumParticle.WATER_SPLASH, owner.getLocation().add(0, 0.5, 0),
                        0.3f, 0.3f, 0.3f, 0, 60, 96, owner.getWorld().getPlayers());
                ticks++;
            }
        }, 0L, 0L);
    }

    @EventHandler
    public void cancelOwnerAttack(SmashDamageEvent e) {
        if(!active) {
            return;
        }
        if(e.getDamager() == null) {
            return;
        }
        if(!e.getDamager().equals(owner)) {
            return;
        }
        if(e.getDamageCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }
        e.setCancelled(true);
    }

}
