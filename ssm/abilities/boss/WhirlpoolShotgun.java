package ssm.abilities.boss;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import ssm.abilities.Ability;
import ssm.managers.ownerevents.OwnerRightClickEvent;
import ssm.projectiles.original.WhirlpoolProjectile;
import ssm.utilities.VelocityUtil;

public class WhirlpoolShotgun extends Ability implements OwnerRightClickEvent {

    protected int projectile_amount = 7;

    public WhirlpoolShotgun() {
        super();
        this.name = "Whirlpool Shotgun";
        this.cooldownTime = 5;
        this.usage = AbilityUsage.RIGHT_CLICK;
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




