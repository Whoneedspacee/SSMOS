package SSM.Abilities;

import SSM.Attributes.Attribute;
import SSM.Attributes.DoubleJumps.DirectDoubleJump;
import SSM.Attributes.DoubleJumps.DoubleJump;
import SSM.Attributes.DoubleJumps.GenericDoubleJump;
import SSM.Events.RechargeAttributeEvent;
import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.Disguise;
import SSM.GameManagers.Disguises.VillagerDisguise;
import SSM.GameManagers.KitManager;
import SSM.GameManagers.OwnerEvents.OwnerDropItemEvent;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Kits.Kit;
import SSM.Utilities.ServerMessageType;
import SSM.Utilities.Utils;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CycleArts extends Ability implements OwnerRightClickEvent, OwnerDropItemEvent {

    private List<Attribute> arts = new ArrayList<Attribute>();
    private Attribute base_form = new BaseForm();
    private Attribute current_form = null;
    private Attribute selected = null;
    private long last_activation_time_ms = 0;
    private int sounds = 0;
    protected long duration_ms = 10000;

    public CycleArts() {
        super();
        this.name = "Cycle Arts";
        this.usage = AbilityUsage.RIGHT_CLICK_DROP;
        this.item_name = ChatColor.RED + "" + ChatColor.BOLD + "Butcher";
        this.cooldownTime = 0;
        this.useMessage = null;
        current_form = base_form;
        arts.add(new ButcherForm());
        arts.add(new BlacksmithForm());
        arts.add(new SpeedsterForm());
        selected = arts.get(0);
        this.description = new String[]{
                ChatColor.RESET + "Use your schooling from villager academy to hone in on one of",
                ChatColor.RESET + "three arts you specialized in and that give you different stats.",
                ChatColor.RESET + "Press right click to switch between arts and drop to activate.",
        };
        this.runTaskTimer(plugin, 0L, 0L);
    }

    @Override
    public void run() {
        if (owner == null) {
            cancel();
            return;
        }
        long time_elapsed = System.currentTimeMillis() - last_activation_time_ms;
        if (current_form != base_form && time_elapsed < duration_ms) {
            owner.setExp(1 - (float) time_elapsed / duration_ms);
            if (time_elapsed > duration_ms - 2000 && sounds == 0 || time_elapsed > duration_ms - 1000 && sounds == 1) {
                owner.playSound(owner.getLocation(), Sound.ORB_PICKUP, 1f, 0.3f);
                sounds++;
            }
            return;
        }
        if (current_form != base_form) {
            owner.setExp(0);
            owner.playSound(owner.getLocation(), Sound.ORB_PICKUP, 1f, 0.5f);
            if (current_form instanceof VillagerArt) {
                VillagerArt art = (VillagerArt) current_form;
                Utils.sendServerMessageToPlayer("Your " + art.getColor() +
                        current_form.name + ChatColor.GRAY + " Art ended.", owner, ServerMessageType.GAME);
            }
            base_form.checkAndActivate();
            current_form = base_form;
        }
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        base_form.setOwner(owner);
        for (Attribute attribute : arts) {
            attribute.setOwner(owner);
        }
        selected = arts.get((arts.indexOf(selected) + 1) % arts.size());
        updateItem();
    }

    @Override
    public void onOwnerDropItem(PlayerDropItemEvent e) {
        checkAndActivate();
    }

    public void activate() {
        sounds = 0;
        base_form.setOwner(owner);
        for (Attribute attribute : arts) {
            attribute.setOwner(owner);
        }
        boolean had_cooldown = selected.hasCooldown();
        selected.checkAndActivate();
        if (!had_cooldown) {
            if (current_form instanceof VillagerArt) {
                VillagerArt art = (VillagerArt) current_form;
                Utils.sendServerMessageToPlayer("You deactivated the " + art.getColor() +
                        current_form.name + ChatColor.GRAY + " Art.", owner, ServerMessageType.GAME);
            }
            last_activation_time_ms = System.currentTimeMillis();
            current_form = selected;
            if (current_form instanceof VillagerArt) {
                VillagerArt art = (VillagerArt) current_form;
                Utils.sendServerMessageToPlayer("You activated the " + art.getColor() +
                        current_form.name + ChatColor.GRAY + " Art.", owner, ServerMessageType.GAME);
            }
        }
        updateItem();
    }

    public void updateItem() {
        Kit kit = KitManager.getPlayerKit(owner);
        if (kit != null && selected instanceof VillagerArt) {
            VillagerArt art = (VillagerArt) selected;
            kit.setItem(art.getItem(), 7);
            // Set item name
            int slot = -1;
            for (int i = 0; i < 9; i++) {
                if (kit.getAbilityInSlot(i).equals(this)) {
                    slot = i;
                    break;
                }
            }
            if (slot != -1) {
                ItemStack item = owner.getInventory().getItem(slot);
                if(item == null) {
                    return;
                }
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName("§e§l" + usage + "§f§l" + " - " + "§a§l" +
                        art.getColor() + ChatColor.BOLD + selected.name);
                item.setItemMeta(meta);
            }
        }
    }

    public Attribute getCurrentForm() {
        return current_form;
    }

    @EventHandler
    public void rechargeAttribute(RechargeAttributeEvent e) {
        if (owner == null) {
            return;
        }
        if (e.getOwner().equals(owner)) {
            updateItem();
        }
    }

    public interface VillagerArt {

        ItemStack getItem();

        ChatColor getColor();

    }

    public static class BaseForm extends Attribute {

        public BaseForm() {
            super();
            this.name = "Base";
            this.cooldownTime = 0;
            this.useMessage = null;
        }

        @Override
        public void activate() {
            Kit kit = KitManager.getPlayerKit(owner);
            if (kit == null) {
                return;
            }
            owner.setWalkSpeed(0.2f);
            kit.setMelee(5.5);
            kit.setArmor(5);
            kit.setArmorSlot(Material.CHAINMAIL_BOOTS, 0);
            kit.setArmorSlot(Material.CHAINMAIL_LEGGINGS, 1);
            kit.setArmorSlot(Material.CHAINMAIL_CHESTPLATE, 2);
            owner.getInventory().setHelmet(null);
            kit.setKnockback(1.45);
            DoubleJump current = kit.getAttributeByClass(DoubleJump.class);
            int old_jumps = current.getRemainingDoubleJumps();
            kit.removeAttribute(current);
            DoubleJump doubleJump = new GenericDoubleJump(0.9, 0.9, 1, Sound.GHAST_FIREBALL);
            doubleJump.setRemainingDoubleJumps(old_jumps);
            kit.addAttribute(doubleJump);
            Disguise disguise = DisguiseManager.disguises.get(owner);
            if (!(disguise instanceof VillagerDisguise)) {
                return;
            }
            VillagerDisguise villagerDisguise = (VillagerDisguise) disguise;
            villagerDisguise.setFarmer();
        }

    }

    public static class ButcherForm extends Attribute implements VillagerArt {

        public ButcherForm() {
            super();
            this.name = "Butcher";
            this.cooldownTime = 25;
            this.useMessage = null;
        }

        @Override
        public void activate() {
            Kit kit = KitManager.getPlayerKit(owner);
            if (kit == null) {
                return;
            }
            Utils.playFirework(owner.getLocation().add(0., 1, 0), FireworkEffect.Type.BALL, Color.RED, true, true);
            owner.setWalkSpeed(0.2f);
            kit.setMelee(7.15);
            kit.setArmor(4.5);
            kit.setArmorSlot(Material.GOLD_BOOTS, 0);
            kit.setArmorSlot(Material.GOLD_LEGGINGS, 1);
            kit.setArmorSlot(Material.GOLD_CHESTPLATE, 2);
            owner.getInventory().setHelmet(null);
            kit.setKnockback(1.45);
            DoubleJump current = kit.getAttributeByClass(DoubleJump.class);
            int old_jumps = current.getRemainingDoubleJumps();
            kit.removeAttribute(current);
            DoubleJump doubleJump = new GenericDoubleJump(0.9, 0.9, 1, Sound.GHAST_FIREBALL);
            doubleJump.setRemainingDoubleJumps(old_jumps);
            kit.addAttribute(doubleJump);
            Disguise disguise = DisguiseManager.disguises.get(owner);
            if (!(disguise instanceof VillagerDisguise)) {
                return;
            }
            VillagerDisguise villagerDisguise = (VillagerDisguise) disguise;
            villagerDisguise.setButcher();
        }

        @Override
        public ItemStack getItem() {
            if (hasCooldown()) {
                return new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.RED.getData());
            }
            return new ItemStack(Material.INK_SACK, 1, (short) (15 - DyeColor.RED.getData()));
        }

        @Override
        public ChatColor getColor() {
            return ChatColor.RED;
        }
    }

    public static class BlacksmithForm extends Attribute implements VillagerArt {

        public BlacksmithForm() {
            super();
            this.name = "Blacksmith";
            this.cooldownTime = 25;
            this.useMessage = null;
        }

        @Override
        public void activate() {
            Kit kit = KitManager.getPlayerKit(owner);
            if (kit == null) {
                return;
            }
            Utils.playFirework(owner.getLocation().add(0., 1, 0), FireworkEffect.Type.BALL, Color.ORANGE, true, true);
            owner.setWalkSpeed(0.16f);
            kit.setMelee(5);
            kit.setArmor(7.5);
            kit.setArmorSlot(Material.DIAMOND_BOOTS, 0);
            kit.setArmorSlot(Material.DIAMOND_LEGGINGS, 1);
            kit.setArmorSlot(Material.IRON_CHESTPLATE, 2);
            owner.getInventory().setHelmet(null);
            kit.setKnockback(0.9);
            DoubleJump current = kit.getAttributeByClass(DoubleJump.class);
            int old_jumps = current.getRemainingDoubleJumps();
            kit.removeAttribute(current);
            DoubleJump doubleJump = new GenericDoubleJump(0.7, 0.7, 1, Sound.GHAST_FIREBALL);
            doubleJump.setRemainingDoubleJumps(old_jumps);
            kit.addAttribute(doubleJump);
            Disguise disguise = DisguiseManager.disguises.get(owner);
            if (!(disguise instanceof VillagerDisguise)) {
                return;
            }
            VillagerDisguise villagerDisguise = (VillagerDisguise) disguise;
            villagerDisguise.setBlacksmith();
        }

        @Override
        public ItemStack getItem() {
            if (hasCooldown()) {
                return new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.ORANGE.getData());
            }
            return new ItemStack(Material.INK_SACK, 1, (short) (15 - DyeColor.ORANGE.getData()));
        }

        @Override
        public ChatColor getColor() {
            return ChatColor.GOLD;
        }
    }

    public static class SpeedsterForm extends Attribute implements VillagerArt {

        public SpeedsterForm() {
            super();
            this.name = "Speedster";
            this.cooldownTime = 25;
            this.useMessage = null;
        }

        @Override
        public void activate() {
            Kit kit = KitManager.getPlayerKit(owner);
            if (kit == null) {
                return;
            }
            Utils.playFirework(owner.getLocation().add(0., 1, 0), FireworkEffect.Type.BALL, Color.LIME, true, true);
            owner.setWalkSpeed(0.24f);
            kit.setMelee(4.5);
            kit.setArmor(4);
            owner.getInventory().setBoots(null);
            owner.getInventory().setLeggings(null);
            kit.setArmorSlot(Material.DIAMOND_CHESTPLATE, 2);
            owner.getInventory().setHelmet(null);
            kit.setKnockback(1.45);
            DoubleJump current = kit.getAttributeByClass(DoubleJump.class);
            int old_jumps = current.getRemainingDoubleJumps();
            kit.removeAttribute(current);
            DoubleJump doubleJump = new DirectDoubleJump(0.9, 0.9, 1, Sound.GHAST_FIREBALL);
            doubleJump.setRemainingDoubleJumps(old_jumps);
            kit.addAttribute(doubleJump);
            Disguise disguise = DisguiseManager.disguises.get(owner);
            if (!(disguise instanceof VillagerDisguise)) {
                return;
            }
            VillagerDisguise villagerDisguise = (VillagerDisguise) disguise;
            villagerDisguise.setLibrarian();
        }

        @Override
        public ItemStack getItem() {
            if (hasCooldown()) {
                return new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.LIME.getData());
            }
            return new ItemStack(Material.INK_SACK, 1, (short) (15 - DyeColor.LIME.getData()));
        }

        @Override
        public ChatColor getColor() {
            return ChatColor.GREEN;
        }
    }

}