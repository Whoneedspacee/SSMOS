package SSM;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Kit {

    // used for finding the kit to equip on command, ex: /kit name
    public String name;
    public double damage;
    public double knockback;
    public double regeneration;
    public double speed;

    // list of materials for armor
    protected ItemStack[] armor;

    // list of materials for weapons
    protected ItemStack[] weapons;

    // assigned to the weapons above by index, ex: 1st ability goes on the 1st weapon, etc
    public Ability[] abilities;

    public Kit() {
        name = "";
        damage = 0;
        knockback = 0;
        regeneration = 0;
        speed = 0;
        armor = new ItemStack[4];
        weapons = new ItemStack[9];
        abilities = new Ability[9];
    }

    public void equipKit(Player player) {
        player.getInventory().clear();
        for (ItemStack item : armor) {
            if (item == null) {
                continue;
            }
            ItemMeta meta = item.getItemMeta();
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
        }
        player.getInventory().setArmorContents(armor);
        for (int i = 0; i < weapons.length; i++) {
            ItemStack item = weapons[i];
            Ability ability = abilities[i];
            if (item == null) {
                continue;
            }
            ItemMeta meta = item.getItemMeta();
            if (ability != null) {
                meta.setDisplayName(ability.name);
            }
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
            player.getInventory().addItem(item);
        }
    }

}
