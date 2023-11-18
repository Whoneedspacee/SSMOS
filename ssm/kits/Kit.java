package ssm.kits;

import ssm.abilities.Ability;
import ssm.attributes.Attribute;
import ssm.events.GameStateChangeEvent;
import ssm.managers.DisguiseManager;
import ssm.Main;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import ssm.managers.GameManager;
import ssm.managers.gamestate.GameState;
import ssm.managers.smashscoreboard.SmashScoreboard;
import ssm.managers.smashserver.SmashServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Kit implements Listener {

    /*  You must add your new kits to the allkits list in the main SSM.java file! */

    // used for finding the kit to equip on command, ex: /kit name
    protected String name = "None";
    protected double damage = 0;
    protected double armor = 0;
    protected double knockback = 0;
    protected double regeneration = 0;
    protected boolean invincible = false;
    protected boolean intangible = false;
    protected Material menuItem;
    protected EntityType podium_mob_type = EntityType.SILVERFISH;

    protected List<Attribute> attributes = new ArrayList<Attribute>();
    protected Ability[] hotbarAbilities = new Ability[9];

    protected JavaPlugin plugin;
    protected Player owner;
    private boolean created = false;
    private boolean preview_hotbar_equipped = false;
    private boolean game_hotbar_equipped = false;
    private boolean playing = false;

    // Kits are singleton, do not create them a second time
    public Kit() {
        this.plugin = Main.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    // Don't call this directly, use KitManager equip
    public void setOwner(Player player) {
        if (created) {
            Bukkit.broadcastMessage(ChatColor.RED + "Tried to equip same kit instance twice.");
            return;
        }
        if (player == null) {
            Bukkit.broadcastMessage(ChatColor.RED + "Tried to equip null player.");
            return;
        }
        created = true;
        owner = player;
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        player.setExp(0);
        hotbarAbilities = new Ability[9];
        player.setGameMode(GameMode.ADVENTURE);
        initializeKit();
        SmashServer server = GameManager.getPlayerServer(owner);
        if(server == null) {
            updatePlaying(GameState.GAME_PLAYING, true);
            return;
        }
        updatePlaying(server.getState(), true);
    }

    protected abstract void initializeKit();

    public void updatePlaying(short new_state, boolean reload_hotbar) {
        if(owner == null) {
            return;
        }
        boolean game_hotbar = GameState.isStarting(new_state) || GameState.isPlaying(new_state);
        // Set hotbar and register or unregister events for attributes
        // Use booleans so we don't re-equip the same hotbar we already did
        if(game_hotbar && (!game_hotbar_equipped || reload_hotbar)) {
            owner.getInventory().clear();
            setGameHotbar();
            game_hotbar_equipped = true;
            preview_hotbar_equipped = false;
        }
        if(!game_hotbar && (!preview_hotbar_equipped || reload_hotbar)) {
            owner.getInventory().clear();
            setPreviewHotbar();
            preview_hotbar_equipped = true;
            game_hotbar_equipped = false;
        }
        if(GameState.isPlaying(new_state)) {
            playing = true;
            for(Attribute attribute : attributes) {
                HandlerList.unregisterAll(attribute);
                Bukkit.getPluginManager().registerEvents(attribute, plugin);
            }
        }
        else {
            playing = false;
            for(Attribute attribute : attributes) {
                HandlerList.unregisterAll(attribute);
            }
        }
    }

    public abstract void setPreviewHotbar();

    public abstract void setGameHotbar();

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
        SmashServer server = GameManager.getPlayerServer(owner);
        if(server == null) {
            updatePlaying(GameState.GAME_PLAYING, false);
            return;
        }
        updatePlaying(server.getState(), false);
    }

    public void removeAttribute(Attribute attribute) {
        attributes.remove(attribute);
        attribute.remove();
    }

    // 0 = boots, 1 = leggings, 2 = chestplate, 3 = helmet
    public void setArmorSlot(Material itemMaterial, int armorSlot) {
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

    public void setAbility(Ability ability, int hotbarSlot) {
        Ability old = hotbarAbilities[hotbarSlot];
        if(old != null) {
            attributes.remove(old);
            old.remove();
        }
        addAttribute(ability);
        hotbarAbilities[hotbarSlot] = ability;
        ItemStack item = owner.getInventory().getItem(hotbarSlot);
        if(item != null) {
            setItem(item, hotbarSlot);
        }
    }

    public void setItem(ItemStack item, int hotbarSlot) {
        setItem(item, hotbarSlot, hotbarAbilities[hotbarSlot]);
    }

    public void setItem(ItemStack item, int hotbarSlot, Attribute attribute) {
        if (owner == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (attribute != null) {
            String used_name = attribute.name;
            if(attribute.item_name != null) {
                used_name = attribute.item_name;
            }
            meta.setDisplayName("§e§l" + attribute.getUsage().toString() + "§f§l" + " - " + "§a§l" + used_name);
            meta.setLore(Arrays.asList(attribute.getDescription()));
        }
        if (meta instanceof Damageable) {
            Damageable damageable = (Damageable) meta;
            damageable.damage((int) damage);
        }
        meta.spigot().setUnbreakable(true);
        item.setItemMeta(meta);
        owner.getInventory().setItem(hotbarSlot, item);
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

    public void setArmor(double armor) {
        this.armor = armor;
    }

    public double getArmor() {
        return armor;
    }

    public void setKnockback(double knockback) {
        this.knockback = knockback;
    }

    public double getKnockback() {
        return knockback;
    }

    public void setMelee(double damage) {
        this.damage = damage;
    }

    public double getMelee() {
        return damage;
    }

    public void setInvincible(boolean set) {
        invincible = set;
    }

    public boolean isInvincible() {
        return invincible;
    }

    public void setIntangible(boolean set) {
        intangible = set;
    }

    public boolean isIntangible() {
        return intangible;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public <T extends Attribute> T getAttributeByClass(Class<T> check) {
        // Check for exact class
        for(Attribute attribute : attributes) {
            if(attribute.getClass() == check) {
                return check.cast(attribute);
            }
        }
        // Check if attribute is subclass of check
        for(Attribute attribute : attributes) {
            if(check.isInstance(attribute)) {
                return check.cast(attribute);
            }
        }
        return null;
    }

    // Preferably use the above method to get by class
    public Attribute getAttributeByName(String name) {
        for(Attribute attribute : attributes) {
            if(attribute.name.equals(name)) {
                return attribute;
            }
        }
        return null;
    }

    public Ability getAbilityInSlot(int inventorySlot) {
        return hotbarAbilities[inventorySlot];
    }

    public boolean getGameHotbarEquipped() {
        return game_hotbar_equipped;
    }

    public boolean isActive() {
        return playing;
    }

    public Entity getNewPodiumMob(Location spawn_location) {
        CraftWorld craftWorld = (CraftWorld) spawn_location.getWorld();
        CraftEntity podium_mob = craftWorld.createEntity(spawn_location, podium_mob_type.getEntityClass()).getBukkitEntity();
        craftWorld.getHandle().addEntity(podium_mob.getHandle());
        net.minecraft.server.v1_8_R3.Entity nms_podium_mob = podium_mob.getHandle();
        nms_podium_mob.onGround = true;
        nms_podium_mob.inWater = false;
        return podium_mob;
    }

    @EventHandler
    public void onGameStateChange(GameStateChangeEvent e) {
        SmashServer server = GameManager.getPlayerServer(owner);
        if(!e.getServer().equals(server)) {
            return;
        }
        updatePlaying(e.getNewState(), false);
    }

}
