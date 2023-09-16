package SSM.Attributes;

import SSM.Attribute;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

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
        task = this.runTaskTimer(plugin, 0, (long) (delay * 20));
    }

    @Override
    public void run() {
        checkAndActivate();
    }

    public void activate() {
        for (ItemStack check : owner.getInventory().getContents()) {
            if (check != null && check.getType() == item && check.getAmount() >= maxAmount) {
                return;
            }
        }
        owner.getInventory().addItem(new ItemStack(item, amount));
        owner.playSound(owner.getLocation(), Sound.ITEM_PICKUP, 10L, 1L);
    }

}
