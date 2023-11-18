package ssm.abilities;

import ssm.managers.ownerevents.OwnerRightClickEvent;
import ssm.utilities.LineParticle;
import ssm.utilities.Utils;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerInteractEvent;

public class Blink extends Ability implements OwnerRightClickEvent {

    protected double range = 16;

    public Blink() {
        super();
        this.name = "Blink";
        this.usage = AbilityUsage.RIGHT_CLICK;
        this.cooldownTime = 7;
        this.description = new String[] {
                ChatColor.RESET + "Instantly teleport in the direction",
                ChatColor.RESET + "you are looking.",
                ChatColor.RESET + "",
                ChatColor.RESET + "You cannot pass through blocks.",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        LineParticle lineParticle = new LineParticle(owner.getEyeLocation(), owner.getLocation().getDirection(),
                0.2f, range, EnumParticle.SMOKE_NORMAL, owner.getWorld().getPlayers());
        while (!lineParticle.update()) { }
        Utils.playFirework(owner.getEyeLocation(), FireworkEffect.Type.BALL, Color.BLACK, false, false);
        owner.playSound(owner.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1f);
        owner.teleport(lineParticle.getDestination().add(0, 0.4, 0));
        owner.setFallDistance(0);
        owner.playSound(owner.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1f);
        Utils.playFirework(owner.getEyeLocation(), FireworkEffect.Type.BALL, Color.BLACK, false, false);
    }

}