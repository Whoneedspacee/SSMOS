package SSM.Kits;

import SSM.Abilities.Ability;
import SSM.Attributes.Attribute;
import SSM.GameManagers.DisguiseManager;
import SSM.SSM;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public abstract class Kit {

    /*  You must add your new kits to the allkits list in the main SSM.java file! */

    // used for finding the kit to equip on command, ex: /kit name
    protected String name = "";
    protected double damage = 0;
    protected double armor = 0;
    protected double knockback = 0;
    protected double regeneration = 0;
    protected Material menuItem;

    protected boolean hasDirectDoubleJump = false;

    protected List<Attribute> attributes = new ArrayList<Attribute>();
    protected Ability[] hotbarAbilities = new Ability[9];

    protected JavaPlugin plugin;
    protected Player owner;

    public Kit() {
        this.plugin = SSM.getInstance();
    }

    public void equipKit(Player player) {
        destroyKit();
        owner = player;
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        player.setExp(0);
        hotbarAbilities = new Ability[9];
    }

    public void destroyKit() {
        DisguiseManager.removeDisguise(owner);
        owner = null;
        for (Attribute attribute : attributes) {
            attribute.remove();
        }
        attributes.clear();
    }

    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
        attribute.setOwner(owner);
    }

    // 0 = boots, 1 = leggings, 2 = chestplate, 3 = helmet
    public void setArmor(Material itemMaterial, int armorSlot) {
        if (owner == null) {
            return;
        }
        ItemStack item = new ItemStack(itemMaterial);
        ItemMeta meta = item.getItemMeta();
        meta.spigot().setUnbreakable(true);
        item.setItemMeta(meta);
        ItemStack[] armor = owner.getInventory().getArmorContents();
        armor[armorSlot] = item;
        owner.getInventory().setArmorContents(armor);
    }

    public void setItem(Material itemMaterial, int inventorySlot) {
        setItem(itemMaterial, inventorySlot, null);
    }

    public void setItem(Material itemMaterial, int inventorySlot, Ability ability) {
        if (owner == null) {
            return;
        }
        ItemStack item = new ItemStack(itemMaterial);
        ItemMeta meta = item.getItemMeta();
        if (ability != null) {
            addAttribute(ability);
            meta.setDisplayName("§e§l" + ability.getUsage().toString() + "§f§l" + " - " + "§a§l" + ability.name);
        }
        if (meta instanceof Damageable) {
            Damageable damageable = (Damageable) meta;
            damageable.damage((int) damage);
        }
        meta.spigot().setUnbreakable(true);
        item.setItemMeta(meta);
        owner.getInventory().setItem(inventorySlot, item);
        hotbarAbilities[inventorySlot] = ability;
    }

    public ItemStack getMenuItemStack() {
        return new ItemStack(menuItem, 1);
    }

    public Material getMenuItemType() {
        return menuItem;
    }

    public String getName() {
        return name;
    }

    public double getArmor() {
        return armor;
    }

    public double getKnockback() {
        return knockback;
    }

    public double getMelee() {
        return damage;
    }

    public void setMelee(double melee) {
        damage = melee;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public Ability getAbilityInSlot(int inventorySlot) {
        return hotbarAbilities[inventorySlot];
    }

    public boolean hasDirectDoubleJump() {
        return hasDirectDoubleJump;
    }
}
