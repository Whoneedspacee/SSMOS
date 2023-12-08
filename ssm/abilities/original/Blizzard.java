package ssm.abilities.original;

import ssm.abilities.Ability;
import ssm.attributes.ExpCharge;
import ssm.events.SmashDamageEvent;
import ssm.managers.BlockRestoreManager;
import ssm.managers.KitManager;
import ssm.kits.Kit;
import ssm.utilities.DamageUtil;
import ssm.utilities.VelocityUtil;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;

public class Blizzard extends Ability {

    private int tick_count = 0;
    protected HashMap<Player, Long> last_damage_time = new HashMap<Player, Long>();
    public int ticks_to_fire = 3;
    public float energy_per_shot = 0.1111111111f;
    public float minimum_energy_required = 0.1f;

    public Blizzard() {
        super();
        this.name = "Blizzard";
        this.usage = AbilityUsage.BLOCKING;
        this.description = new String[]{
                ChatColor.RESET + "Release a windy torrent of snow, able",
                ChatColor.RESET + "to blow opponents off the stage.",
                ChatColor.RESET + "",
                ChatColor.RESET + "" + ChatColor.AQUA + "Blizzard uses Energy (Experience Bar)",
        };
        this.runTaskTimer(plugin, 0L, 0L);
    }

    @Override
    public void run() {
        Kit kit = KitManager.getPlayerKit(owner);
        if (kit != null) {
            ExpCharge charge = kit.getAttributeByClass(ExpCharge.class);
            charge.enabled = !owner.isBlocking();
        }
        if (tick_count % ticks_to_fire == 0) {
            checkAndActivate();
        }
        tick_count = (tick_count + 1) % ticks_to_fire;
    }

    public void activate() {
        if (!owner.isBlocking()) {
            return;
        }
        if (owner.getExp() < minimum_energy_required) {
            return;
        }
        owner.setExp(Math.max(0, owner.getExp() - energy_per_shot));
        for (int i = 0; i < 4; i++) {
            Snowball snowball = owner.getWorld().spawn(owner.getEyeLocation().add(owner.getLocation().getDirection()), Snowball.class);
            double x = 0.1 - ((Math.floor(Math.random() * 20)) / 100d);
            double y = (Math.floor(Math.random() * 20)) / 100d;
            double z = 0.1 - (Math.floor(Math.random() * 20) / 100d);
            snowball.setShooter(owner);
            snowball.setVelocity(owner.getLocation().getDirection().add(new Vector(x, y, z)).multiply(2));
            snowball.setMetadata("Blizzard", new FixedMetadataValue(plugin, 1));
        }
        owner.getWorld().playSound(owner.getLocation(), Sound.STEP_SNOW, 0.1f, 0.5f);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onSnowballHit(SmashDamageEvent e) {
        if (e.getDamageCause() != EntityDamageEvent.DamageCause.PROJECTILE) {
            return;
        }
        Projectile projectile = e.getProjectile();
        if(!projectile.getShooter().equals(owner)) {
            return;
        }
        List<MetadataValue> data = projectile.getMetadata("Blizzard");
        if (data.size() <= 0) {
            return;
        }
        e.setCancelled(true);
        if(!DamageUtil.canDamage(e.getDamagee(), owner)) {
            return;
        }
        VelocityUtil.setVelocity(e.getDamagee(), projectile.getVelocity().multiply(0.15).add(new Vector(0, 0.15, 0)));
        if (e.getDamagee() instanceof Player) {
            Player player = (Player) e.getDamagee();
            if (last_damage_time.containsKey(player)) {
                if(System.currentTimeMillis() - last_damage_time.get(player) < 200) {
                    return;
                }
            }
            last_damage_time.put(player, System.currentTimeMillis());
            SmashDamageEvent smashDamageEvent = new SmashDamageEvent(e.getDamagee(), owner, 1);
            smashDamageEvent.multiplyKnockback(0);
            smashDamageEvent.setIgnoreDamageDelay(true);
            smashDamageEvent.setReason(name);
            smashDamageEvent.callEvent();
        }
    }

    @EventHandler
    public void snowballForm(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Snowball)) {
            return;
        }
        Snowball snowball = (Snowball) e.getEntity();
        if(!snowball.getShooter().equals(owner)) {
            return;
        }
        List<MetadataValue> data = e.getEntity().getMetadata("Blizzard");
        if (data.size() <= 0) {
            return;
        }
        BlockRestoreManager.ourInstance.snow(e.getEntity().getLocation().getBlock(), (byte) 1, (byte) 7, 2000, 250, 0);
    }

}