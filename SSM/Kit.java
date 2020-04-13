package SSM;

import me.libraryaddict.disguise.LibsDisguises;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Kit{

    /*  You must add your new kits to the allkits list in the main SSM.java file! */

    // used for finding the kit to equip on command, ex: /kit name
    protected String name = "";
    protected int damage = 0;
    protected double armor = 0;
    protected double knockback = 0;
    protected double regeneration = 0;
    protected float speed = 0f;
    protected DisguiseType disguise;

    protected boolean hasDirectDoubleJump = false;
    protected double doubleJumpHeight = 0.8;
    protected double doubleJumpPower = 0.61;

    protected List<Attribute> attributes = new ArrayList<Attribute>();

    protected Plugin plugin;
    protected Player owner;

    public Kit() {
        this.plugin = SSM.getInstance();
    }

    public void equipKit(Player player) {
        destroyKit();
        owner = player;
        player.setWalkSpeed(speed);
        player.getInventory().clear();
        player.setExp(0);
        MobDisguise disg = new MobDisguise(disguise);
        disg.setEntity(player);
        disg.startDisguise();
    }

    public void destroyKit() {
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
        meta.setUnbreakable(true);
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
            meta.setDisplayName(ability.name);
        }
        if (meta instanceof Damageable) {
            Damageable damageable = (Damageable) meta;
            damageable.setDamage(damage);
        }
        meta.setUnbreakable(true);
        item.setItemMeta(meta);
        owner.getInventory().setItem(inventorySlot, item);
    }

    public String getName() {
        return name;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public boolean hasDirectDoubleJump() {
        return hasDirectDoubleJump;
    }

    public double getDoubleJumpHeight() {
        return doubleJumpHeight;
    }

    public double getDoubleJumpPower() {
        return doubleJumpPower;
    }
}
