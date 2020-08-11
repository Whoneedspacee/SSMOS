package SSM;

import SSM.GameManagers.CooldownManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class Ability extends Attribute {

    protected double cooldownTime = 2.5;
    protected boolean usesEnergy = false;
    protected float expUsed = 0;
    protected float energy = 0;
    protected float xp;

    public Ability() {
        super();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void checkAndActivate(Player player) {
<<<<<<< HEAD
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getItemMeta() == null) {
            return;
        }
        String itemName = item.getItemMeta().getDisplayName();

        if (CooldownManager.getInstance().getRemainingTimeFor(itemName, player) <= 0) {
            if (itemName.equalsIgnoreCase(name)) {
                CooldownManager.getInstance().addCooldown(itemName, (long) (cooldownTime * 1000), player);
                if (usesEnergy) {
                    energy = owner.getExp();
                    xp = (owner.getExpToLevel() * expUsed) / owner.getExpToLevel();
                    if (xp >= owner.getExp()) {
                        return;
                    }
                    owner.setExp(owner.getExp() - (xp));
                    activate();
                } else {
                    activate();
=======
        if (CooldownManager.getInstance().getRemainingTimeFor(name, player) <= 0) {
            CooldownManager.getInstance().addCooldown(name, (long) (cooldownTime * 1000), player);
            if (usesEnergy) {
                energy = owner.getExp();
                xp = (owner.getExpToLevel() * expUsed) / owner.getExpToLevel();
                if (xp >= owner.getExp()) {
                    return;
>>>>>>> master
                }
                owner.setExp(owner.getExp() - (xp));
            }
            activate();
        }
    }

<<<<<<< HEAD
    public static void setCooldown(String abilityName, Player abilityUser, long duration) {
        for (CooldownManager.CooldownData cd : CooldownManager.cooldownData) {
            if (cd.abilityName.contains(abilityName)) {
                CooldownManager.cooldownData.remove(cd);
            }
            CooldownManager.cooldownData.add(new CooldownManager.CooldownData(abilityName, duration, abilityUser));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
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
    }
}

=======
    public abstract void activate();

}
>>>>>>> master
