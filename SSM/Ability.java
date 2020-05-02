package SSM;

import SSM.GameManagers.CooldownManager;
import org.bukkit.EntityEffect;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public abstract class Ability extends Attribute {

    protected double cooldownTime = 2.5;
    protected double useTime = 3;
    protected double timeLeft;
    protected boolean leftClickActivate = false;
    protected boolean rightClickActivate = false;
    protected boolean holdDownActivate = false;
    protected boolean usesEnergy = false;
    protected float expUsed = 0;

    float energy = 0;
    float xp;

    public Ability() {
        super();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public abstract void activate();

    public void activateLeft(Player player) { //This activates an ability on left click if its set to activate on left click
        if (leftClickActivate) {
            checkAndActivate(player);
        }
    }

    public void activateRight(Player player) { //This activates an ability on right click if its set to activate on right click
        if (rightClickActivate) {
            checkAndActivate(player);
        }
    }

    public void activateHold(Player player) { //This activates an ability while right click is held
        if (holdDownActivate) {
            checkAndActivate(player);
        }
    }

    public void checkAndActivate(Player player) { //This is the code for activating an ability and starting the cooldown
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getItemMeta() == null) {
            return;
        }
        String itemName = item.getItemMeta().getDisplayName();

        if (CooldownManager.getInstance().getRemainingTimeFor(itemName, player) <= 0) {
            if (itemName.contains(name)) {
                if (holdDownActivate) {
                    for (timeLeft = useTime; timeLeft >= 0; timeLeft--) {
                        player.playEffect(EntityEffect.SHIELD_BLOCK);
                        player.getServer().broadcastMessage("" + player.isBlocking());
                        CooldownManager.getInstance().addCooldown(itemName, (long) (cooldownTime * 1000), player);
                        activate();
                    }
                }
                    CooldownManager.getInstance().addCooldown(itemName, (long) (cooldownTime * 1000), player);
                    if (usesEnergy) {
                        energy = owner.getExp();
                        xp = (owner.getExpToLevel() * expUsed) / owner.getExpToLevel();
                        if (xp >= owner.getExp()) {
                            return;
                        }
                        owner.setExp(owner.getExp() - (xp));
                        activate();
                    }
                    activate();
            }
        }
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) { //This is the code for detecting if a player left or right clicks
        Player player = e.getPlayer();
        if (player != owner) {
            return;
        }
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            activateLeft(player);
        }
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            activateRight(player);
        }
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            activateRight(player);
        }
    }
}
