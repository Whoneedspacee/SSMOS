package ssm.kits;

import ssm.attributes.Compass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class KitTemporarySpectator extends Kit implements Listener {

    public KitTemporarySpectator() {
        super();
        this.name = "Temporary Spectator";
        invincible = true;
        intangible = true;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void initializeKit() {
        addAttribute(new Compass());

        owner.setAllowFlight(true);
        owner.setFlying(true);

        // Hide player, prevents projectile and melee hitting
        for (Player hidefrom : Bukkit.getOnlinePlayers()) {
            if (owner.equals(hidefrom)) {
                continue;
            }
            hidefrom.hidePlayer(owner);
        }
    }

    public void destroyKit() {
        if (owner != null) {
            // Unhide owner
            for (Player hidefrom : Bukkit.getOnlinePlayers()) {
                if (owner.equals(hidefrom)) {
                    continue;
                }
                hidefrom.showPlayer(owner);
            }
            owner.setFlying(false);
            owner.setAllowFlight(false);
        }
        super.destroyKit();
    }

    @Override
    public void setPreviewHotbar() {
        return;
    }

    @Override
    public void setGameHotbar() {
        setItem(Compass.COMPASS_ITEM, 0);
    }

}
