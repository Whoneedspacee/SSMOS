package xyz.whoneedspacee.ssmos.abilities.ssmos;

import xyz.whoneedspacee.ssmos.managers.ownerevents.OwnerRightClickEvent;
import xyz.whoneedspacee.ssmos.projectiles.ssmos.MagmaBoulderProjectile;
import xyz.whoneedspacee.ssmos.abilities.Ability;
import org.bukkit.*;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.whoneedspacee.ssmos.utilities.VelocityUtil;
import xyz.whoneedspacee.ssmos.attributes.Attribute;

public class MagmaBoulder extends Ability implements OwnerRightClickEvent {

    private int task = -1;

    public MagmaBoulder() {
        super();
        this.name = "Magma Boulder";
        this.cooldownTime = 10;
        this.usage = Attribute.AbilityUsage.RIGHT_CLICK;
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
