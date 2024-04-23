package xyz.whoneedspacee.ssmos.abilities.boss;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import xyz.whoneedspacee.ssmos.managers.ownerevents.OwnerRightClickEvent;
import xyz.whoneedspacee.ssmos.projectiles.original.WhirlpoolProjectile;
import xyz.whoneedspacee.ssmos.abilities.Ability;
import xyz.whoneedspacee.ssmos.utilities.VelocityUtil;
import xyz.whoneedspacee.ssmos.attributes.Attribute;

public class WhirlpoolShotgun extends Ability implements OwnerRightClickEvent {

    protected int projectile_amount = 7;

    public WhirlpoolShotgun() {
        super();
        this.name = "Whirlpool Shotgun";
        this.cooldownTime = 5;
        this.usage = Attribute.AbilityUsage.RIGHT_CLICK;
        this.description = new String[] {
                ChatColor.RESET + "Blasts 7 whirlpool axes out at high velocity.",
                ChatColor.RESET + "They explode upon hitting something, dealing",
                ChatColor.RESET + "damage and knockback.",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        for (int i = 0; i < projectile_amount; i++) {
            WhirlpoolProjectile projectile = new WhirlpoolProjectile(owner, name) {
                @Override
                protected void doVelocity() {
                    Vector random = new Vector((Math.random() - 0.5), (Math.random() - 0.5), (Math.random() - 0.5));
                    random.multiply(2);
                    random.normalize();
                    random.multiply(0.15);
                    VelocityUtil.setVelocity(projectile, firer.getLocation().getDirection().add(random),
                            1 + 0.4 * Math.random(), false, 0, 0.2, 10, false);
                }
            };
            projectile.launchProjectile();
        }
        owner.getWorld().playSound(owner.getLocation(), Sound.EXPLODE, 1.5f, 0.75f);
    }

}




