package ssm.abilities.original;

import ssm.abilities.Ability;
import ssm.events.SmashDamageEvent;
import ssm.managers.ownerevents.OwnerRightClickEvent;
import ssm.utilities.DamageUtil;
import ssm.utilities.ServerMessageType;
import ssm.utilities.Utils;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;

public class BoneExplosion extends Ability implements OwnerRightClickEvent {

    protected double range = 7;
    protected double baseDamage = 6;

    public BoneExplosion() {
        super();
        this.name = "Bone Explosion";
        this.cooldownTime = 10;
        this.description = new String[] {
                ChatColor.RESET + "Releases an explosion of bones from",
                ChatColor.RESET + "your body, repelling all nearby enemies."};
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        Location location = owner.getLocation().add(0, 0.5, 0);
        Utils.itemEffect(owner.getLocation().add(0, 0.5, 0), 48, 0.8,
                Sound.SKELETON_HURT, 2f, 1.2f, Material.BONE, (byte) 0, 40);

        HashMap<LivingEntity, Double> canHit = Utils.getInRadius(owner.getLocation(), range);
        canHit.remove(owner);
        for (LivingEntity other : canHit.keySet()) {
            if ((other instanceof Player)) {
                Player hit = (Player) other;
                double damage = baseDamage * canHit.get(hit);
                if (!DamageUtil.canDamage(hit, owner)) {
                    continue;
                }
                SmashDamageEvent smashDamageEvent = new SmashDamageEvent(hit, owner, damage);
                smashDamageEvent.multiplyKnockback(2.5);
                smashDamageEvent.setIgnoreDamageDelay(true);
                smashDamageEvent.setReason(name);
                smashDamageEvent.callEvent();
                Utils.sendAttributeMessage(ChatColor.YELLOW + owner.getName() +
                        ChatColor.GRAY + " used", name, hit, ServerMessageType.GAME);
            }
        }
    }
}