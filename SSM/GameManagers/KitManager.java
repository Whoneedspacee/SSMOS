package SSM.GameManagers;

import SSM.Abilities.Ability;
import SSM.Attributes.Attribute;
import SSM.Kits.Kit;
import SSM.Kits.*;
import SSM.SSM;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class KitManager implements CommandExecutor, Listener {

    private static KitManager ourInstance;
    private static HashMap<UUID, Kit> playerKit = new HashMap<UUID, Kit>();
    private List<Kit> allKits = new ArrayList<Kit>();
    private JavaPlugin plugin = SSM.getInstance();

    public KitManager() {
        allKits.add(new KitSkeleton());
        allKits.add(new KitIronGolem());
        allKits.add(new KitSpider());
        allKits.add(new KitSlime());
        allKits.add(new KitSquid());
        allKits.add(new KitCreeper());
        allKits.add(new KitSnowMan());
        allKits.add(new KitWolf());
        allKits.add(new KitMagmaCube());
        allKits.add(new KitWitch());
        allKits.add(new KitCow());
        allKits.add(new KitPig());
        allKits.add(new KitBlaze());
        allKits.add(new KitZombie());
        allKits = Collections.unmodifiableList(allKits);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.getCommand("kit").setExecutor(this);
        ourInstance = this;
    }

    public static void equipPlayer(Player player, Kit check) {
        Kit kit = playerKit.get(player.getUniqueId());
        if (kit != null) {
            kit.destroyKit();
        }
        try {
            kit = check.getClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        if (kit != null) {
            kit.equipKit(player);
        }
        playerKit.put(player.getUniqueId(), kit);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("kit")) {
            if (!(sender instanceof Player)) {
                return true;
            }
            Player player = (Player) sender;
            Inventory selectKit = Bukkit.createInventory(player, 54, ChatColor.BLUE + "Select Your Kit");
            for (Kit kit : KitManager.getAllKits()) {
                ItemStack item = new ItemStack(kit.menuItem);
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(ChatColor.RESET + kit.getName().replace("_", " "));
                item.setItemMeta(itemMeta);
                selectKit.addItem(item);
                player.openInventory(selectKit);
            }
            return true;
        }
        return false;
    }

    @EventHandler
    public void clickEvent(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        if (item == null) {
            return;
        }
        if (e.getView().getTitle().contains("Kit")) {
            for (Kit kit : KitManager.getAllKits()) {
                if (item.getType() == kit.menuItem) {
                    KitManager.equipPlayer(player, kit);
                    player.closeInventory();
                    break;
                }
            }
            e.setCancelled(true);
        }
    }

    public static Ability getCurrentAbility(Player player) {
        Kit kit = KitManager.getPlayerKit(player);
        if (kit == null) {
            return null;
        }
        List<Attribute> attributes = kit.getAttributes();
        if(player.getInventory().getItemInHand().getItemMeta() == null) {
            return null;
        }
        int currentSlot = player.getInventory().getHeldItemSlot();
        return kit.getAbilityInSlot(currentSlot);
    }

    public static Kit getPlayerKit(Player player) {
        return KitManager.getInstance().playerKit.get(player.getUniqueId());
    }

    public static List<Kit> getAllKits() {
        return KitManager.getInstance().allKits;
    }

    public static KitManager getInstance() {
        return ourInstance;
    }

}
