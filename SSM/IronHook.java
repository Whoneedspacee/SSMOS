package me.SirInHueman.SSM;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class IronHook {
    ItemStack hook;
    Item ent;
    Entity name;
    public void ability(Player player){
        name = player;
        hook = new ItemStack(Material.TRIPWIRE_HOOK, 1);
        ent = player.getWorld().dropItem(player.getEyeLocation(), hook);
        ent.setCustomName("Iron Hook");
        ent.setPickupDelay(1000000);
        ent.setVelocity(player.getLocation().getDirection().multiply(1.2));


    }
}
