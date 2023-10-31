package SSM.GameManagers;

import SSM.Abilities.Ability;
import SSM.Attributes.Attribute;
import SSM.Events.SmashDamageEvent;
import SSM.GameManagers.OwnerEvents.*;
import SSM.Kits.Kit;
import SSM.SSM;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;

import java.util.List;

public class EventManager implements Listener {

    private static EventManager ourInstance;
    private JavaPlugin plugin = SSM.getInstance();

    public EventManager() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        ourInstance = this;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Ability ability = KitManager.getCurrentAbility(player);
        if (ability == null) {
            return;
        }
        if (ability instanceof OwnerRightClickEvent && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            OwnerRightClickEvent rightClick = (OwnerRightClickEvent) ability;
            rightClick.onOwnerRightClick(e);
        }
    }

    // left click in adventure mode for skeleton + zombie
    // the reason why it randomly doesn't work sometimes
    @EventHandler
    public void onPlayerLeftClick(PlayerAnimationEvent e) {
        Player player = e.getPlayer();
        Ability ability = KitManager.getCurrentAbility(player);
        if (ability == null) {
            return;
        }
        if (ability instanceof OwnerLeftClickEvent && (e.getAnimationType() == PlayerAnimationType.ARM_SWING)) {
            OwnerLeftClickEvent leftClick = (OwnerLeftClickEvent) ability;
            leftClick.onOwnerLeftClick(e);
        }
    }

    @EventHandler
    public void onSmashDamage(SmashDamageEvent e) {
        Entity damagee = e.getDamagee();
        Entity damager = e.getDamager();
        if (damager instanceof Player) {
            Player player = (Player) damager;
            if(KitManager.getPlayerKit(player) == null) {
                return;
            }
            List<Attribute> attributes = KitManager.getPlayerKit(player).getAttributes();
            for (Attribute attribute : attributes) {
                if (attribute instanceof OwnerDealSmashDamageEvent) {
                    OwnerDealSmashDamageEvent damageEntityEvent = (OwnerDealSmashDamageEvent) attribute;
                    damageEntityEvent.onOwnerDealSmashDamageEvent(e);
                }
            }
        }
        if(damagee instanceof Player) {
            Player player = (Player) damagee;
            if(KitManager.getPlayerKit(player) == null) {
                return;
            }
            List<Attribute> attributes = KitManager.getPlayerKit(player).getAttributes();
            for (Attribute attribute : attributes) {
                if (attribute instanceof OwnerTakeSmashDamageEvent) {
                    OwnerTakeSmashDamageEvent damageOwnerEvent = (OwnerTakeSmashDamageEvent) attribute;
                    damageOwnerEvent.onOwnerTakeSmashDamageEvent(e);
                }
            }
        }
    }

    @EventHandler
    public void checkFiredBow(EntityShootBowEvent e) {
        if(e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            Kit kit = KitManager.getPlayerKit(player);
            if(kit.isActive()) {
                return;
            }
            e.setCancelled(true);
        }
    }

    public static EventManager getInstance() {
        return ourInstance;
    }

}
