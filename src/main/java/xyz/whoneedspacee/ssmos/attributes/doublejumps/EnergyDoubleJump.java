package xyz.whoneedspacee.ssmos.attributes.doublejumps;

import org.bukkit.GameMode;
import xyz.whoneedspacee.ssmos.kits.Kit;
import xyz.whoneedspacee.ssmos.managers.KitManager;
import org.bukkit.Sound;

public abstract class EnergyDoubleJump extends DoubleJump {

    public EnergyDoubleJump(double power, double height, Sound double_jump_sound, float expUsed) {
        super(power, height, double_jump_sound);
        this.name = "Energy Double Jump";
        this.recharge_delay_ms = 100;
        this.expUsed = expUsed;
        this.needsExactXP = false;
    }

    @Override
    public void run() {
        if(owner == null) {
            return;
        }
        if(owner.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        Kit kit = KitManager.getPlayerKit(owner);
        if(kit != null && !kit.isActive()) {
            owner.setAllowFlight(false);
            return;
        }
        if(groundCheck()) {
            owner.setAllowFlight(true);
        }
        else if(System.currentTimeMillis() - last_jump_time_ms >= recharge_delay_ms && owner.getExp() > 0) {
            owner.setAllowFlight(true);
        }
    }

}
