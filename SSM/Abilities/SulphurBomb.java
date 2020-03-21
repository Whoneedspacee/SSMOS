package SSM.Abilities;

import SSM.*;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class SulphurBomb extends Ability {

    public SulphurBomb(Plugin plugin) {
        super(plugin);
        this.name = "Sulphur Bomb";
        this.cooldownTime = 3;
        this.rightClickActivate = true;
    }

    public void useAbility(Player player) {
        new ItemProjectile(plugin, player, name, Material.COAL, 6.0, 2.0, 2.0, 1, 0, true);
    }

}
