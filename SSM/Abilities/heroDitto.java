package SSM.Abilities;

import SSM.Ability;
import SSM.Kit;
import SSM.SSM;
import SSM.Attribute;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class heroDitto extends Ability {

    public heroDitto(){
        this.name = "Ditto!";
        this.cooldownTime = 0;
        this.rightClickActivate = true;
        this.item = Material.PURPLE_WOOL;
        this.expUsed = 0.5F;
        this.usesEnergy = true;
    }

    public void activate() {
        for (int i = 1; i < 27;i++){
            owner.getInventory().clear(i);
        }
        List<Entity> nearby = owner.getNearbyEntities(10, 10, 10);
        nearby.remove(owner);
        if (nearby.isEmpty()){
            owner.sendMessage("There were no nearby players to turn into!");
            return;
        }
        for (Entity entity : nearby){
            if (!(entity instanceof Player)){
                continue;
            }
            Player target = (Player)entity;
            Kit targetKit = SSM.playerKit.get(target.getUniqueId());
            Kit playerKit = SSM.playerKit.get(owner.getUniqueId());
            for (Attribute ability : targetKit.getAttributes()){
                if (ability.name.equalsIgnoreCase("Regeneration") || ability.name.equalsIgnoreCase("Double Jump")){
                    continue;
                }
                ItemStack item = new ItemStack(Material.NETHER_STAR);
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(ability.name);
                item.setItemMeta(itemMeta);
                playerKit.addAttribute(ability);
                owner.getInventory().addItem(item);
            }
        }


    }
}
