package xyz.whoneedspacee.ssmos.managers;

import xyz.whoneedspacee.ssmos.managers.ownerevents.*;
import xyz.whoneedspacee.ssmos.abilities.Ability;
import xyz.whoneedspacee.ssmos.attributes.Attribute;
import xyz.whoneedspacee.ssmos.events.SmashDamageEvent;
import xyz.whoneedspacee.ssmos.kits.Kit;
import xyz.whoneedspacee.ssmos.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class EventManager implements Listener {

    private static EventManager ourInstance;
    private JavaPlugin plugin = Main.getInstance();

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
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        if(KitManager.getPlayerKit(player) == null) {
            return;
        }
        List<Attribute> attributes = KitManager.getPlayerKit(player).getAttributes();
        List<OwnerDropItemEvent> to_call = new ArrayList<>();
        for (Attribute attribute : attributes) {
            if (attribute instanceof OwnerDropItemEvent) {
                OwnerDropItemEvent dropEvent = (OwnerDropItemEvent) attribute;
                to_call.add(dropEvent);
            }
        }
        for(OwnerDropItemEvent dropEvent : to_call) {
            dropEvent.onOwnerDropItem(e);
        }
    }

    @EventHandler
    public void onToggleSneak(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();
        if(KitManager.getPlayerKit(player) == null) {
            return;
        }
        List<Attribute> attributes = KitManager.getPlayerKit(player).getAttributes();
        for (Attribute attribute : attributes) {
            if (attribute instanceof OwnerToggleSneakEvent) {
                OwnerToggleSneakEvent sneakEvent = (OwnerToggleSneakEvent) attribute;
                sneakEvent.onOwnerToggleSneak(e);
            }
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
