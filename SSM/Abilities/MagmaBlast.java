package SSM.Abilities;

import SSM.Ability;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import org.bukkit.Sound;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class MagmaBlast extends Ability implements OwnerRightClickEvent {

    private LargeFireball largeFireball;

    public MagmaBlast() {
        super();
        this.name = "Magma Blast";
        this.cooldownTime = 8;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_CREEPER_DEATH, 1, 1);
        largeFireball = owner.launchProjectile(LargeFireball.class);
        largeFireball.setCustomName("MagmaBlast");
        largeFireball.setVelocity(owner.getEyeLocation().getDirection().multiply(1.0D));
        Double x = owner.getPlayer().getEyeLocation().getDirection().getX() * -1.36;
        Double z = owner.getPlayer().getEyeLocation().getDirection().getZ() * -1.36;
        Double y = owner.getPlayer().getEyeLocation().getDirection().getY() * -1.36;
        owner.setVelocity(new Vector(x, y, z));
    }
}
