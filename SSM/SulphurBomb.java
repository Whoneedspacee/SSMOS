package me.SirInHueman.SSM;




import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;


public class SulphurBomb{
    ItemStack sulphur;
    Item ent;
    Entity name;
    public void ability(Player player){
        name = player;
        sulphur = new ItemStack(Material.COAL, 1);
        ent = player.getWorld().dropItem(player.getEyeLocation(), sulphur);
        ent.setCustomName("Sulphur Bomb");
        ent.setPickupDelay(1000000);
        ent.setVelocity(player.getLocation().getDirection().multiply(1.3));







    }
}
