package SSM.Abilities;

import SSM.*;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class SulphurBomb extends Ability {

    public SulphurBomb() {
        this.name = "Sulphur Bomb";
        this.cooldownTime = 3;
        this.rightClickActivate = true;
    }

    public void useAbility(Player player) {
        ItemStack sulphur = new ItemStack(Material.COAL, 1);
        Item ent = player.getWorld().dropItem(player.getEyeLocation(), sulphur);
        ent.setCustomName("Sulphur Bomb");
        ent.setPickupDelay(1000000);
        ent.setVelocity(player.getLocation().getDirection().multiply(1.3));
    }

}
