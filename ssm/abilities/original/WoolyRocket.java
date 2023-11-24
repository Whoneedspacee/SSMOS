package ssm.abilities.original;

import ssm.abilities.Ability;
import ssm.attributes.doublejumps.DoubleJump;
import ssm.events.SmashDamageEvent;
import ssm.managers.CooldownManager;
import ssm.managers.DisguiseManager;
import ssm.managers.disguises.Disguise;
import ssm.managers.disguises.SheepDisguise;
import ssm.managers.KitManager;
import ssm.managers.ownerevents.OwnerRightClickEvent;
import ssm.kits.Kit;
import ssm.utilities.DamageUtil;
import ssm.utilities.Utils;
import ssm.utilities.VelocityUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class WoolyRocket extends Ability implements OwnerRightClickEvent {

    private int rocket_task = -1;
    protected double damage = 8;
    protected double damage_radius = 2;
    protected double knockback_multiplier = 2.5;
    protected long minimum_velocity_time_ms = 200;
    protected long maximum_velocity_time_ms = 1200;

    public WoolyRocket() {
        super();
        this.name = "Wooly Rocket";
        this.cooldownTime = 10;
        this.description = new String[] {
                ChatColor.RESET + "Become like a cloud and shoot yourself",
                ChatColor.RESET + "directly upwards. You deal damage to anyone",
                ChatColor.RESET + "you collide with on your ascent.",
                ChatColor.RESET + "",
                ChatColor.RESET + "Using this recharges your Double Jump.",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        VelocityUtil.setVelocity(owner, new Vector(0, 1, 0), 1, false, 0, 0, 2, true);
        owner.getWorld().playEffect(owner.getLocation(), Effect.BLAZE_SHOOT, 0);
        Kit kit = KitManager.getPlayerKit(owner);
        if(kit != null) {
            DoubleJump doubleJump = kit.getAttributeByClass(DoubleJump.class);
            if(doubleJump != null) {
                doubleJump.resetDoubleJumps();
            }
        }
        setWoolColor(DyeColor.RED);
        if(Bukkit.getScheduler().isQueued(rocket_task) || Bukkit.getScheduler().isCurrentlyRunning(rocket_task)) {
            Bukkit.getScheduler().cancelTask(rocket_task);
        }
        rocket_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(owner == null) {
                    Bukkit.getScheduler().cancelTask(rocket_task);
                    return;
                }
                Utils.playParticle(EnumParticle.FLAME, owner.getLocation(),
                        0.2f, 0.2f, 0.2f, 0, 4, 96, owner.getWorld().getPlayers());
                long time_elapsed = CooldownManager.getInstance().getTimeElapsedFor(WoolyRocket.this, owner);
                if(time_elapsed < minimum_velocity_time_ms) {
                    return;
                }
                for(Player player : owner.getWorld().getPlayers()) {
                    if(player.equals(owner)) {
                        continue;
                    }
                    if(!DamageUtil.canDamage(player, owner)) {
                        continue;
                    }
                    if(player.getLocation().distance(owner.getLocation()) < damage_radius) {
                        SmashDamageEvent smashDamageEvent = new SmashDamageEvent(player, owner, damage);
                        smashDamageEvent.multiplyKnockback(knockback_multiplier);
                        smashDamageEvent.setReason(name);
                        smashDamageEvent.callEvent();
                        Utils.playParticle(EnumParticle.EXPLOSION_NORMAL, player.getLocation(),
                                0f, 0f, 0f, 0, 1, 96, player.getWorld().getPlayers());
                        Utils.playParticle(EnumParticle.LAVA, player.getLocation(),
                                0.2f, 0.2f, 0.2f, 0, 10, 96, player.getWorld().getPlayers());
                    }
                }
                if(Utils.entityIsOnGround(owner) || time_elapsed >= maximum_velocity_time_ms) {
                    Bukkit.getScheduler().cancelTask(rocket_task);
                    setWoolColor(DyeColor.WHITE);
                    return;
                }
            }
        }, 0L, 0L);
    }

    public void setWoolColor(DyeColor color) {
        Disguise disguise = DisguiseManager.disguises.get(owner);
        if(!(disguise instanceof SheepDisguise)) {
            return;
        }
        SheepDisguise sheepDisguise = (SheepDisguise) disguise;
        sheepDisguise.setColor(color);
    }

}