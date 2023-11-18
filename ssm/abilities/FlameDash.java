package ssm.abilities;

import ssm.events.SmashDamageEvent;
import ssm.managers.DisguiseManager;
import ssm.managers.disguises.Disguise;
import ssm.managers.disguises.MagmaCubeDisguise;
import ssm.managers.ownerevents.OwnerRightClickEvent;
import ssm.utilities.DamageUtil;
import ssm.utilities.ServerMessageType;
import ssm.utilities.Utils;
import ssm.utilities.VelocityUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class FlameDash extends Ability implements OwnerRightClickEvent {

    private long activation_time = 0;
    private Location initial_location = null;
    private int dash_task = -1;
    protected long dash_time_ms = 800;
    protected double damage_radius = 3;
    protected double dash_knockback = 2;

    public FlameDash() {
        super();
        this.name = "Flame Dash";
        this.cooldownTime = 8;
        this.description = new String[]{
                ChatColor.RESET + "Disappear in flames, and fly horizontally",
                ChatColor.RESET + "in the direction you are looking. You explode",
                ChatColor.RESET + "when you re-appear, dealing damage to enemies.",
                ChatColor.RESET + "",
                ChatColor.RESET + "Damage increases with distance travelled.",
                ChatColor.RESET + "",
                ChatColor.RESET + "Right-Click again to end Flame Dash early.",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        // Prevent spam click cancelling instantly
        if (System.currentTimeMillis() - activation_time < 100) {
            return;
        }
        if (Bukkit.getScheduler().isQueued(dash_task) || Bukkit.getScheduler().isCurrentlyRunning(dash_task)) {
            Bukkit.getScheduler().cancelTask(dash_task);
            Utils.sendAttributeMessage("You ended", name, owner, ServerMessageType.SKILL);
            dashEnd();
            return;
        }
        checkAndActivate();
    }

    public void activate() {
        activation_time = System.currentTimeMillis();
        initial_location = owner.getLocation();
        disguiseHide();
        dash_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(owner == null) {
                    Bukkit.getScheduler().cancelTask(dash_task);
                    return;
                }
                if (System.currentTimeMillis() - activation_time < dash_time_ms) {
                    Vector velocity = initial_location.getDirection();
                    velocity.setY(0);
                    velocity.normalize();
                    velocity.setY(0.05);
                    VelocityUtil.setVelocity(owner, velocity);
                    owner.getWorld().playSound(owner.getLocation(), Sound.FIZZ, 0.6f, 1.2f);
                    Utils.playParticle(EnumParticle.FLAME, owner.getLocation().add(0, 0.4, 0),
                            0.2f, 0.2f, 0.2f, 0f, 3, 96, owner.getWorld().getPlayers());
                    return;
                }
                Bukkit.getScheduler().cancelTask(dash_task);
                dashEnd();
            }
        }, 0L, 0L);
    }

    public void dashEnd() {
        HashMap<LivingEntity, Double> hit_entities = Utils.getInRadius(owner.getLocation(), damage_radius);
        for (LivingEntity livingEntity : hit_entities.keySet()) {
            if(!(livingEntity instanceof Player)) {
                continue;
            }
            Player player = (Player) livingEntity;
            if (player.equals(owner)) {
                continue;
            }
            double dist = owner.getLocation().distance(initial_location) / 2;
            double damage = 2 + dist;
            if(!DamageUtil.canDamage(player, owner)) {
                continue;
            }
            SmashDamageEvent smashDamageEvent = new SmashDamageEvent(player, owner, damage);
            smashDamageEvent.multiplyKnockback(dash_knockback);
            smashDamageEvent.setIgnoreDamageDelay(true);
            smashDamageEvent.setReason(name);
            smashDamageEvent.callEvent();
            Utils.sendAttributeMessage(ChatColor.YELLOW + owner.getName() +
                    ChatColor.GRAY + " hit you with", name, player, ServerMessageType.GAME);
        }
        disguiseShow();
        owner.getWorld().playSound(owner.getLocation(), Sound.EXPLODE, 1f, 1.2f);
        Utils.playParticle(EnumParticle.FLAME, owner.getLocation(),
                0.1f, 0.1f, 0.1f, 0.3f, 100, 96, owner.getWorld().getPlayers());
        Utils.playParticle(EnumParticle.EXPLOSION_LARGE, owner.getLocation().add(0, 0.4, 0),
                0.2f, 0.2f, 0.2f, 0, 1, 96, owner.getWorld().getPlayers());
    }

    public void disguiseHide() {
        Disguise disguise = DisguiseManager.disguises.get(owner);
        if (!(disguise instanceof MagmaCubeDisguise)) {
            return;
        }
        MagmaCubeDisguise magmaCubeDisguise = (MagmaCubeDisguise) disguise;
        magmaCubeDisguise.deleteLiving();
        magmaCubeDisguise.hideOwner();
    }

    public void disguiseShow() {
        Disguise disguise = DisguiseManager.disguises.get(owner);
        if (!(disguise instanceof MagmaCubeDisguise)) {
            return;
        }
        MagmaCubeDisguise magmaCubeDisguise = (MagmaCubeDisguise) disguise;
        magmaCubeDisguise.spawnLiving();
    }

}