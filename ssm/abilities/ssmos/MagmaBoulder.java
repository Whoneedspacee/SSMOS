package ssm.abilities.ssmos;

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
import ssm.projectiles.ssmos.MagmaBoulderProjectile;
import ssm.utilities.ServerMessageType;
import ssm.utilities.Utils;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import ssm.utilities.VelocityUtil;

public class MagmaBoulder extends Ability implements OwnerRightClickEvent {

    private int task = -1;

    public MagmaBoulder() {
        super();
        this.name = "Magma Boulder";
        this.cooldownTime = 10;
        this.usage = AbilityUsage.RIGHT_CLICK;
        this.description = new String[] {
                ChatColor.RESET + "Create a massive boulder of magma that",
                ChatColor.RESET + "gains power as it bounces dealing",
                ChatColor.RESET + "bonus damage and knockback.",
                ChatColor.RESET + "",
                ChatColor.RESET + "Deals extra knockback to aerial opponents.",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        VelocityUtil.setVelocity(owner, owner.getLocation().getDirection().multiply(-1), 1.2, false, 0, 0.2, 1.2, true);
        MagmaBoulderProjectile projectile = new MagmaBoulderProjectile(owner, name, 87, (byte) 0);
        projectile.launchProjectile();
    }

}
