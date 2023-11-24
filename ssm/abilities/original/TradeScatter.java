package ssm.abilities.original;

import ssm.abilities.Ability;
import ssm.attributes.Attribute;
import ssm.managers.KitManager;
import ssm.managers.ownerevents.OwnerRightClickEvent;
import ssm.kits.Kit;
import ssm.projectiles.ScatterProjectile;
import ssm.utilities.VelocityUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class TradeScatter extends Ability implements OwnerRightClickEvent {

    protected int bullets = 10;

    public TradeScatter() {
        super();
        this.name = "Trade Scatter";
        this.cooldownTime = 7;
        this.description = new String[] {
                ChatColor.RESET + "After a hard days work of trading with the players,",
                ChatColor.RESET + "you unload your goods upon your enemies,",
                ChatColor.RESET + "propelling you back or forth depending on your trade skills",
                ChatColor.RESET + "and throwing your favorite items in the opposite direction."
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        Kit kit = KitManager.getPlayerKit(owner);
        if(kit == null) {
            return;
        }
        CycleArts cycleArts = kit.getAttributeByClass(CycleArts.class);
        if(cycleArts == null) {
            return;
        }
        Location location = null;
        Vector direction = null;
        double player_velocity = 1.25;
        double bullet_velocity = 2;
        double bullet_spread = 0.2;
        double bullet_damage = 0.5;
        long tick_effect_rate = 1;
        boolean front = false;
        EnumParticle particle_type = EnumParticle.VILLAGER_HAPPY;
        Material bullet_item = Material.EMERALD;
        Attribute currentForm = cycleArts.getCurrentForm();
        if(currentForm instanceof CycleArts.ButcherForm) {
            front = true;
            particle_type = EnumParticle.FLAME;
            bullet_item = Material.RAW_BEEF;
            bullet_damage = 1;
            tick_effect_rate = 3;
            player_velocity = 1;
        } else if (currentForm instanceof CycleArts.BlacksmithForm) {
            front = true;
            bullet_spread = 0.75;
            particle_type = EnumParticle.FIREWORKS_SPARK;
            bullet_item = Material.IRON_INGOT;
            player_velocity = 1;
        } else if (currentForm instanceof CycleArts.SpeedsterForm) {
            particle_type = EnumParticle.ENCHANTMENT_TABLE;
            bullet_item = Material.FEATHER;
            player_velocity = 1.5;
            //bullet_velocity = 2;
        }
        if(front) {
            location = owner.getEyeLocation();
            direction = location.getDirection();
            player_velocity *= -1;
        } else {
            location = owner.getLocation().add(0, 1.3, 0);
            direction = location.getDirection().multiply(-bullet_velocity);
        }
        VelocityUtil.setVelocity(owner, player_velocity, 0.3, 1.2, true);
        owner.getWorld().playSound(location, Sound.FIREWORK_LARGE_BLAST, 1.5f, 1f);
        for(int i = 0; i< bullets; i++) {
            ScatterProjectile projectile = new ScatterProjectile(owner, name);
            Item item = location.getWorld().dropItem(location, new ItemStack(bullet_item));
            projectile.setProjectileEntity(item);
            projectile.setDamage(bullet_damage);
            projectile.projectile_velocity = bullet_velocity;
            projectile.spread = bullet_spread;
            projectile.projectile_direction = direction;
            projectile.tick_effect_rate = tick_effect_rate;
            projectile.particle_type = particle_type;
            projectile.launchProjectile();
        }
    }

}