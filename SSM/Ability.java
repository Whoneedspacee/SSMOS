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
        if (CooldownManager.getInstance().getRemainingTimeFor(name, player) <= 0) {
            CooldownManager.getInstance().addCooldown(name, (long) (cooldownTime * 1000), player);
            if (usesEnergy) {
                energy = owner.getExp();
                xp = (owner.getExpToLevel() * expUsed) / owner.getExpToLevel();
                if (xp >= owner.getExp()) {
                    return;
                }
                owner.setExp(owner.getExp() - (xp));
            }
            activate();
        }
    }

    public abstract void activate();

}