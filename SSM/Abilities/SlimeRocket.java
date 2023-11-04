package SSM.Abilities;

import SSM.Attributes.ExpCharge;
import SSM.Events.SmashDamageEvent;
import SSM.GameManagers.CooldownManager;
import SSM.GameManagers.KitManager;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Kits.Kit;
import SSM.Projectiles.SlimeProjectile;
import SSM.Utilities.DamageUtil;
import SSM.Utilities.ServerMessageType;
import SSM.Utilities.Utils;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

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
            ExpCharge charge = (ExpCharge) kit.getAttributeByName("Exp Charge");
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
                owner.setExp((float) Math.max(0, owner.getExp() - 0.00916f));
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
            ExpCharge charge = (ExpCharge) kit.getAttributeByName("Exp Charge");
            charge.enabled = true;
        }
        Utils.sendServerMessageToPlayer("§7You released §a" + name + "§7.",
                owner, ServerMessageType.SKILL);
        long startTime = CooldownManager.getInstance().getStartTimeFor(this, owner);
        double charge = Math.min(3, (double) (System.currentTimeMillis() - startTime) / 1000d);
        Slime slime = (Slime) owner.getWorld().spawnEntity(owner.getEyeLocation(), EntityType.SLIME);
        slime.setSize(Math.max(1, (int) charge));
        slime.setMaxHealth(5 + charge * 7);
        slime.setHealth(slime.getMaxHealth());
        slime.setMetadata("Slime Owner", new FixedMetadataValue(plugin, owner));
        SlimeProjectile projectile = new SlimeProjectile(owner, "Slime Rocket", charge);
        projectile.setProjectileEntity(slime);
        projectile.launchProjectile();
    }

}
