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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class KitManager implements Listener {

    private static KitManager ourInstance;
    private static HashMap<Player, Kit> playerKit = new HashMap<Player, Kit>();
    private List<Kit> allKits = new ArrayList<Kit>();
    private JavaPlugin plugin = SSM.getInstance();

    public KitManager() {
        allKits.add(new KitSkeleton());
        allKits.add(new KitIronGolem());
        allKits.add(new KitSpider());
        allKits.add(new KitSlime());
        allKits.add(new KitSquid());
        allKits.add(new KitCreeper());
        allKits.add(new KitEnderman());
        allKits.add(new KitSnowMan());
        allKits.add(new KitWolf());
        allKits.add(new KitMagmaCube());
        allKits.add(new KitWitch());
        allKits.add(new KitWitherSkeleton());
        allKits.add(new KitZombie());
        allKits.add(new KitCow());
        allKits.add(new KitSkeletonHorse());
        allKits.add(new KitPig());
        allKits.add(new KitBlaze());
        allKits.add(new KitChicken());
        allKits.add(new KitGuardian());
        allKits.add(new KitSheep());
        allKits.add(new KitVillager());
        allKits = Collections.unmodifiableList(allKits);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        ourInstance = this;
    }

    public static void equipPlayer(Player player, Kit check) {
        Kit kit = playerKit.get(player);
        if(kit != null) {
            unequipPlayer(player);
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
        playerKit.put(player, kit);
        DisplayManager.buildScoreboard();
    }

    public static void unequipPlayer(Player player) {
        Kit kit = playerKit.get(player);
        if(kit == null) {
            return;
        }
        kit.destroyKit();
        playerKit.remove(player);
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[player.getInventory().getArmorContents().length]);
        player.setFlying(false);
        player.setAllowFlight(false);
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
        return playerKit.get(player);
    }

    public static List<Kit> getAllKits() {
        return KitManager.getInstance().allKits;
    }

    public static KitManager getInstance() {
        return ourInstance;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        playerKit.remove(e.getPlayer());
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
                if (item.getType().equals(kit.getMenuItemType())) {
                    KitManager.equipPlayer(player, kit);
                    player.closeInventory();
                    break;
                }
            }
            e.setCancelled(true);
        }
    }

}
