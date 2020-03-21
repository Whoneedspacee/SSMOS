package SSM.Abilities;

import SSM.*;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class SpinWeb extends Ability {

    public SpinWeb(Plugin plugin) {
        super(plugin);
        this.name = "Spin Web";
        this.cooldownTime = 5;
        this.rightClickActivate = true;
    }

    public void useAbility(Player player) {
        player.setVelocity(player.getLocation().getDirection().multiply(1.5));
        new ItemProjectile(plugin, player, name, Material.COBWEB, 15.0, -1.5, 2, 1, 1, 0, true, true);
    }

}
