package SSM.GameManagers;

import SSM.Abilities.Ability;
import SSM.Attributes.Attribute;
import SSM.Kits.Kit;
import SSM.SSM;
import SSM.Utilities.ServerMessageType;
import SSM.Utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A singleton implementation for a Cooldown manager which handles everything about cooldowns including displaying and calculating.
 */

public class CooldownManager extends BukkitRunnable {

    private static CooldownManager ourInstance;
    private ArrayList<CooldownData> cooldownData = new ArrayList<>();
    private boolean isRunning = false;
    private JavaPlugin plugin = SSM.getInstance();

    public CooldownManager() {
        ourInstance = this;
        this.runTaskTimer(plugin, 0, 1);
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

            Ability using = KitManager.getCurrentAbility(currData.getAbilityUser());

            if(using != null && using.equals(currData.getAttribute())) {
                displayCooldownTo(currData.getAbilityUser(), currData);
            }

            if (currData.getRemainingTimeMs() <= 0) {
                cdDataIterator.remove();

                Utils.sendAttributeMessage("You can use", currData.getAttribute().name,
                        currData.getAbilityUser(), ServerMessageType.RECHARGE);
                Utils.sendActionBarMessage("§a§l" + currData.getAttribute().name + " Recharged", currData.getAbilityUser());
                Ability ability = KitManager.getCurrentAbility(currData.getAbilityUser());
                if (ability != null && ability.equals(currData.getAttribute())) {
                    currData.getAbilityUser().playSound(currData.getAbilityUser().getLocation(), Sound.NOTE_PLING, 1.0f, 24.0f);
                }
            }
        }
    }

    public long getRemainingTimeFor(Attribute attribute, Player abilityUser) {
        for (CooldownData cd : cooldownData) {
            if (cd.getAttribute().equals(attribute) && cd.abilityUser.equals(abilityUser)) {
                return cd.getRemainingTimeMs();
            }
        }

        return 0;
    }

    public long getTimeElapsedFor(Attribute attribute, Player abilityUser) {
        for (CooldownData cd : cooldownData) {
            if (cd.getAttribute().equals(attribute) && cd.abilityUser.equals(abilityUser)) {
                return cd.getTimeElapsedMs();
            }
        }

        return 0;
    }

    public long getStartTimeFor(Attribute attribute, Player abilityUser) {
        for (CooldownData cd : cooldownData) {
            if (cd.getAttribute().equals(attribute) && cd.abilityUser.equals(abilityUser)) {
                return cd.getStartTime();
            }
        }

        return 0;
    }

    /**
     * @param attribute     used to reference cooldown (should be ability)
     * @param duration    time in milliseconds for cooldown duration
     * @param abilityUser player using the ability (cooldownData linked with this player)
     */
    public void addCooldown(Attribute attribute, long duration, Player abilityUser) {
        Kit kit = KitManager.getPlayerKit(abilityUser);
        if (duration <= 0) {
            return;
        } else {
            cooldownData.add(new CooldownData(attribute, duration, abilityUser));
            Utils.sendAttributeMessage(attribute.getUseMessage(), attribute.name, abilityUser, ServerMessageType.SKILL);
        }
    }

    /**
     * Create and display the cooldown message shown to players above their action bar when holding an ability
     */
    private void displayCooldownTo(Player player, CooldownData cd) {
        int barLength = 24;
        int startRedBarInterval = barLength - (int) ((cd.getRemainingTimeMs() / (double) cd.duration) * barLength); // Val between 0 - 1
        StringBuilder sb = new StringBuilder("      §f§l" + cd.getAttribute().name + " ");
        for (int i = 0; i < barLength; i++) {
            if (i < startRedBarInterval) {
                sb.append("§a▌");
            } else {
                sb.append("§c▌");
            }
        }

        sb.append(" §f" + Utils.msToSeconds(cd.getRemainingTimeMs()) + " Seconds");
        Utils.sendActionBarMessage(sb.toString(), player);
    }


    public static CooldownManager getInstance() {
        return ourInstance;
    }

    /**
     * Container for all information needed in a cooldown
     */
    private class CooldownData {
        private Player abilityUser;
        private Attribute attribute ;
        private long duration;
        private long startTime;

        CooldownData(Attribute attribute, long duration, Player abilityUser) {
            this.attribute = attribute;
            this.duration = duration;
            startTime = System.currentTimeMillis();
            this.abilityUser = abilityUser;
        }

        long getRemainingTimeMs() {
            return startTime + duration - System.currentTimeMillis();
        }

        long getTimeElapsedMs() { return System.currentTimeMillis() - startTime; }

        long getStartTime() { return startTime; }

        Player getAbilityUser() {
            return abilityUser;
        }

        Attribute getAttribute() {
            return attribute;
        }
    }
}