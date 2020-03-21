package SSM.Abilities;

import SSM.*;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class IronHook extends Ability {

    public IronHook(Plugin plugin) {
        super(plugin);
        this.name = "Iron Hook";
        this.cooldownTime = 10;
        this.rightClickActivate = true;
    }

    public void useAbility(Player player) {
        new ItemProjectile(plugin, player, name, Material.TRIPWIRE_HOOK, 5.0, 2.5, -5, 1, 0, true);
    }

}
