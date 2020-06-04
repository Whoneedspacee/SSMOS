package SSM.GameManagers;

import SSM.Kit;
import SSM.SSM;
import SSM.Utilities.ServerMessageType;
import SSM.Utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * A singleton implementation for a Cooldown manager which handles everything about cooldowns including displaying and calculating.
 */

public class CooldownManager extends BukkitRunnable {
    public static CooldownManager ourInstance = new CooldownManager();
    public static ArrayList<CooldownData> cooldownData = new ArrayList<>();

    private boolean isRunning = false;

    public void start(Plugin plugin) throws ManagerAlreadyRunningException {
        if (isRunning) {
            throw new ManagerAlreadyRunningException();
        }

        this.runTaskTimer(plugin, 1, 1);
        isRunning = true;
    }

    /**
     * Cleans up CooldownData that is finished.
     * Sends information about current state of CooldownData to players in game.
     */
    @Override
    public void run() {
        for (Iterator<CooldownData> cdDataIterator = cooldownData.iterator(); cdDataIterator.hasNext(); ) {
            CooldownData currData = cdDataIterator.next();

            if (currData.getRemainingTimeMs() <= 0) {
                cdDataIterator.remove();

                Utils.sendServerMessageToPlayer("§7You can use §a" + currData.abilityName + "§7.", currData.abilityUser, ServerMessageType.RECHARGE);
                if (Utils.holdingItemWithName(currData.abilityUser, currData.abilityName)) {
                    currData.abilityUser.playSound(currData.abilityUser.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 24.0f);
                }
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (Utils.holdingItemWithName(player, currData.abilityName) && player.equals(currData.abilityUser)) {
                    displayCooldownTo(player, currData);
                }
            }
        }
    }

    public long getRemainingTimeFor(String abilityName, Player abilityUser) {
        for (CooldownData cd : cooldownData) {
            if (cd.abilityName.equals(abilityName) && cd.abilityUser.equals(abilityUser)) {
                return cd.getRemainingTimeMs();
            }
        }

        return 0;
    }

    /**
     * @param abilityName name used to reference cooldown (should be abilityName)
     * @param duration    time in milliseconds for cooldown duration
     * @param abilityUser player using the ability (cooldownData linked with this player)
     */
    public void addCooldown(String abilityName, long duration, Player abilityUser) {
        Kit kit = SSM.playerKit.get(abilityUser.getUniqueId());
        if (duration <= 1000){
            return;
        }
        else{
            cooldownData.add(new CooldownData(abilityName, duration, abilityUser));
        }
    }

    /**
     * Create and display the cooldown message shown to players above their action bar when holding an ability
     */
    private void displayCooldownTo(Player player, CooldownData cd) {
        int barLength = 24;
        int startRedBarInterval = barLength - (int) ((cd.getRemainingTimeMs() / (double) cd.duration) * barLength); // Val between 0 - 1
        StringBuilder sb = new StringBuilder("§f§l" + cd.abilityName + " ");
        for (int i = 0; i < barLength; i++) {
            if (i < startRedBarInterval) {
                sb.append("§a▌");
            } else {
                sb.append("§c▌");
            }
        }

        sb.append("  §f" + Utils.msToSeconds(cd.getRemainingTimeMs()) + " Seconds");
        Utils.sendActionBarMessage(sb.toString(), player);
    }


    public static CooldownManager getInstance() {
        return ourInstance;
    }

    /**
     * Container for all information needed in a cooldown
     */
    public static class CooldownData {
        public Player abilityUser;
        public String abilityName;
        public long duration;
        public long startTime;

        public CooldownData(String abilityName, long duration, Player abilityUser) {
            this.abilityName = abilityName;
            this.duration = duration;
            startTime = System.currentTimeMillis();
            this.abilityUser = abilityUser;
        }

        long getRemainingTimeMs() {
            return startTime + duration - System.currentTimeMillis();
        }

        Player getAbilityUser() {
            return abilityUser;
        }

        String getAbilityName() {
            return abilityName;
        }
    }
}
