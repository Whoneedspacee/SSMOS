package ssm.abilities.original;

import net.minecraft.server.v1_8_R3.EntitySlime;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSlime;
import org.bukkit.scheduler.BukkitRunnable;
import ssm.abilities.Ability;
import ssm.attributes.ExpCharge;
import ssm.managers.CooldownManager;
import ssm.managers.KitManager;
import ssm.managers.ownerevents.OwnerRightClickEvent;
import ssm.kits.Kit;
import ssm.projectiles.original.SlimeProjectile;
import ssm.utilities.ServerMessageType;
import ssm.utilities.Utils;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class SlimeRocket extends Ability implements OwnerRightClickEvent {

    private int task = -1;

    public SlimeRocket() {
        super();
        this.name = "Slime Rocket";
        this.cooldownTime = 6;
        this.usage = AbilityUsage.BLOCKING;
        this.useMessage = "You are charging";
        this.description = new String[] {
                ChatColor.RESET + "Slowly transfer your slimey goodness into",
                ChatColor.RESET + "a new slime. When you release block, the",
                ChatColor.RESET + "new slime is propelled forward.",
                ChatColor.RESET + "",
                ChatColor.RESET + "The more you charge the ability, the stronger",
                ChatColor.RESET + "the new slime is projected forwards.",
                ChatColor.RESET + "",
                ChatColor.RESET + "" + ChatColor.AQUA + "Slime Rocket uses Energy (Experience Bar)",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        Kit kit = KitManager.getPlayerKit(owner);
        if(kit != null) {
            ExpCharge charge = kit.getAttributeByClass(ExpCharge.class);
            charge.enabled = false;
        }
        long activation_ms = System.currentTimeMillis();
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () ->
        {
            if(owner == null) {
                Bukkit.getScheduler().cancelTask(task);
                return;
            }
            if(!owner.isBlocking()) {
                // Fire Slime
                Bukkit.getScheduler().cancelTask(task);
                fireRocket();
                return;
            }
            long elapsed_ms = CooldownManager.getInstance().getTimeElapsedFor(this, owner);
            double elapsed_sound = Math.min(3, (double) (System.currentTimeMillis() - activation_ms) / 1000d);
            if (owner.getExp() < 0.1 || elapsed_ms >= 5000) {
                // Fire Slime
                Bukkit.getScheduler().cancelTask(task);
                fireRocket();
                return;
            }
            if (elapsed_ms < 3000) {
                owner.setExp(Math.max(0, owner.getExp() - 0.00916f));
            }
            owner.getWorld().playSound(owner.getLocation(), Sound.SLIME_WALK, 0.5f, (float) (0.5 + 1.5 * (elapsed_sound / 3d)));
            Utils.playParticle(EnumParticle.SLIME, owner.getLocation().add(0, 1, 0),
                    (float) (elapsed_sound / 6d), (float) (elapsed_sound / 6d), (float) (elapsed_sound / 6d), 0, (int) (elapsed_sound * 5),
                    96, owner.getWorld().getPlayers());
        }, 0L, 0L);
    }

    public void fireRocket() {
        Kit kit = KitManager.getPlayerKit(owner);
        if(kit != null) {
            ExpCharge charge = kit.getAttributeByClass(ExpCharge.class);
            if(charge != null) {
                charge.enabled = true;
            }
        }
        Utils.sendServerMessageToPlayer("§7You released §a" + name + "§7.",
                owner, ServerMessageType.SKILL);
        long startTime = CooldownManager.getInstance().getStartTimeFor(this, owner);
        double charge = Math.min(3, (double) (System.currentTimeMillis() - startTime) / 1000d);
        Slime slime = owner.getWorld().spawn(owner.getEyeLocation(), Slime.class);
        // Hack fix because slimes refuse to face any direction but south
        BukkitRunnable runnable = new BukkitRunnable() {
            private EntitySlime nms_slime = ((CraftSlime) slime).getHandle();
            private float original_yaw = owner.getEyeLocation().getYaw();
            @Override
            public void run() {
                if (nms_slime == null || nms_slime.onGround) {
                    cancel();
                    return;
                }
                nms_slime.yaw = original_yaw;
                nms_slime.lastYaw = original_yaw;
            }
        };
        runnable.runTaskTimer(plugin, 0L, 0L);
        slime.setSize(Math.max(1, (int) charge));
        slime.setMaxHealth(5 + charge * 7);
        slime.setHealth(slime.getMaxHealth());
        slime.leaveVehicle();
        slime.eject();
        slime.setMetadata("Slime Owner", new FixedMetadataValue(plugin, owner));
        SlimeProjectile projectile = new SlimeProjectile(owner, "Slime Rocket", charge);
        projectile.setProjectileEntity(slime);
        projectile.launchProjectile();
    }

}
