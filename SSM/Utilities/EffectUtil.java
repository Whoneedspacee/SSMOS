package SSM.Utilities;

import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class EffectUtil {

    public static void createEffect(Location location, int particles, double velocity, Sound sound, float soundVolume,
                                    float soundPitch, Material type, byte data, int ticks, boolean bloodStep) {
        List<Item> dropped = new ArrayList<Item>();
        for (int i = 0; i < particles; i++) {
            ItemStack stack = new ItemStack(type, 1);
            if (type == Material.INK_SACK) {
                stack.setDurability((short) 1);
            }
            Item item = location.getWorld().dropItem(location, stack);
            item.setVelocity(new Vector((Math.random() - 0.5) * velocity, Math.random() * velocity, (Math.random() - 0.5) * velocity));
            item.setPickupDelay(999999);
            dropped.add(item);
        }
        if (bloodStep) {
            location.getWorld().playEffect(location, Effect.STEP_SOUND, 55);
        }
        location.getWorld().playSound(location, sound, soundVolume, soundPitch);

        Bukkit.getScheduler().runTaskLater(SSM.SSM.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Item item : dropped) {
                    item.remove();
                }
            }
        }, ticks);
    }
}
