package SSM.Attributes;

import SSM.GameManagers.KitManager;
import SSM.Utilities.DamageUtil;
import SSM.Utilities.ServerMessageType;
import SSM.Utilities.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.entity.EntityDamageEvent;

public class Hunger extends Attribute {

    protected double seconds_to_drain;
    protected long hunger_restore_delay_ms;
    protected long hunger_ticks = 0;
    protected long last_hunger_restore_ms = System.currentTimeMillis();

    public Hunger() {
        this(10);
    }

    public Hunger(double seconds_to_drain) {
        this(seconds_to_drain, 250);
    }

    public Hunger(double seconds_to_drain, long hunger_restore_delay_ms) {
        this.name = "Hunger";
        this.seconds_to_drain = seconds_to_drain;
        this.hunger_restore_delay_ms = hunger_restore_delay_ms;
        task = this.runTaskTimer(plugin, 0, (long) 20);
    }

    @Override
    public void run() {
        checkAndActivate();
    }

    public void activate() {
        if (owner.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        hunger_ticks = (hunger_ticks + 1) % 10;
        owner.setSaturation(3f);
        owner.setExhaustion(0f);
        if(owner.getFoodLevel() <= 0) {
            DamageUtil.damage(owner, null, 1, 0, false,
                    EntityDamageEvent.DamageCause.STARVATION, null, "Starvation");
            Utils.sendServerMessageToPlayer("Attack other players to restore hunger!", owner,
                    ServerMessageType.GAME);
        }
        if(hunger_ticks == 0) {
            owner.setFoodLevel(Math.max(0, owner.getFoodLevel() - 1));
        }
    }

    public void hungerRestore(double damage) {
        if((System.currentTimeMillis() - last_hunger_restore_ms) < hunger_restore_delay_ms) {
            return;
        }
        last_hunger_restore_ms = System.currentTimeMillis();
        int amount = Math.max(1, (int) (damage / 2));
        owner.setFoodLevel(Math.min(20, owner.getFoodLevel() + amount));
    }

}
