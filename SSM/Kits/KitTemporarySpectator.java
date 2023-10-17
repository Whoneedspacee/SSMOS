package SSM.Kits;

import SSM.Attributes.Compass;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class KitTemporarySpectator extends Kit implements Listener {

    public KitTemporarySpectator() {
        super();
        this.name = "Temporary Spectator";
        invincible = true;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void equipKit(Player player) {
        super.equipKit(player);

        setItem(Material.COMPASS, 0, null);

        addAttribute(new Compass());

        owner.setAllowFlight(true);
        owner.setFlying(true);

        // Hide player, prevents projectile and melee hitting
        for (Player hidefrom : Bukkit.getOnlinePlayers()) {
            if (player.equals(hidefrom)) {
                continue;
            }
            hidefrom.hidePlayer(player);
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

}
