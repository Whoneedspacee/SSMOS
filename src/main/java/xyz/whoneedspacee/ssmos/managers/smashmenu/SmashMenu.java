package xyz.whoneedspacee.ssmos.managers.smashmenu;

import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class SmashMenu {

    private HashMap<Integer, MenuRunnable> actions = new HashMap<Integer, MenuRunnable>();
    private Inventory inventory;

    public SmashMenu(Inventory inventory) {
        this.inventory = inventory;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setActionFromSlot(int slot, MenuRunnable runnable) {
        actions.put(slot, runnable);
    }

    public MenuRunnable getActionFromSlot(int slot) {
        return actions.get(slot);
    }

}
