package SSM.Attributes;

import SSM.*;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.List;

public class ItemGenerator extends Attribute {

    Material item;
    int amount;
    int maxAmount;
    double delay;

    public ItemGenerator(Material item, int amount, int maxAmount, double delay) {
        super();
        this.name = "Item Generator";
        this.item = item;
        this.amount = amount;
        this.maxAmount = maxAmount;
        this.delay = delay;
        this.runTaskTimer(plugin, 0, (long) delay * 20);
    }

    public void activate() {
        for (ItemStack check : owner.getInventory().getContents()) {
            if (check != null && check.getType() == item && check.getAmount() >= maxAmount) {
                return;
            }
        }
        owner.getInventory().addItem(new ItemStack(item, amount));
    }


    @Override
    public void run() {
        activate();
    }

}
