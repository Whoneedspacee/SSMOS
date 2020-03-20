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
        ItemStack hook = new ItemStack(Material.TRIPWIRE_HOOK, 1);
        Item ent = player.getWorld().dropItem(player.getEyeLocation(), hook);
        ent.setCustomName("Iron Hook");
        ent.setPickupDelay(1000000);
        ent.setVelocity(player.getLocation().getDirection().multiply(1.2));
    }

}
