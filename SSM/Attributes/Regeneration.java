package SSM.Attributes;

import SSM.*;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.List;

public class Regeneration extends Attribute {

    Double regen;
    double delay;

    public Regeneration(Double regen, double delay) {
        super();
        this.name = "Regeneration";
        this.regen = regen;
        this.delay = delay;
        task = this.runTaskTimer(plugin, 0, (long) delay * 20);
    }

    public void activate() {
        owner.setHealth(Math.min(owner.getHealth() + regen, 20));
    }

    @Override
    public void run() {
        activate();
    }

}
