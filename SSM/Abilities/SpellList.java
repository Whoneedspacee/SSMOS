package SSM.Abilities;

import SSM.Ability;
import SSM.SSM;
import SSM.Kit;
import SSM.Attribute;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class SpellList extends Ability {


    public SpellList(){
        this.name = "Spell List";
        this.cooldownTime = 2;
        this.rightClickActivate = true;

    }

    public void activate() {
        for (int i = 0; i < 4; i++) {
            int Random = (int) (Math.random() * SSM.heroAbilities.length);
            Ability spell = SSM.heroAbilities[Random];
            ItemStack item = new ItemStack(spell.item);
            if (owner.getInventory().contains(item.getType())){
                i--;
                continue;
            }
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(spell.name);
            item.setItemMeta(itemMeta);
            owner.getInventory().setItem(i + 1, item);
        }
    }
}


