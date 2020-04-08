package SSM.Abilities;

import SSM.*;
import org.bukkit.event.Listener;;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class SlimeSlam extends Leap {

    private Leap slime;

    public SlimeSlam() {
        this.name = "Slime Slam";
        this.cooldownTime = 6;
        this.rightClickActivate = true;
    }

    public void activate() {
        slime.setPower(1);
        slime.setActiveTime(1.5);
        slime.setDamage(7.0);
        slime.setKnockback(3.5);
        slime.setRecoil(true);
        slime.activate();
        slime.remove();
    }

}
