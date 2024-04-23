package xyz.whoneedspacee.ssmos.abilities.original;

import xyz.whoneedspacee.ssmos.managers.ownerevents.OwnerRightClickEvent;
import xyz.whoneedspacee.ssmos.abilities.Ability;
import xyz.whoneedspacee.ssmos.attributes.NetherPig;
import xyz.whoneedspacee.ssmos.events.SmashDamageEvent;
import xyz.whoneedspacee.ssmos.managers.KitManager;
import xyz.whoneedspacee.ssmos.kits.Kit;
import xyz.whoneedspacee.ssmos.utilities.DamageUtil;
import xyz.whoneedspacee.ssmos.utilities.ServerMessageType;
import xyz.whoneedspacee.ssmos.utilities.Utils;
import xyz.whoneedspacee.ssmos.utilities.VelocityUtil;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;

public class BabyBaconBombs extends Ability implements OwnerRightClickEvent {

    private long last_time_used = 0;
    protected double damage = 4;
    protected double damage_radius = 4;
    protected long cooldown_time_ms = 500;
    protected float base_energy_cost = 0.35f;
    protected float nether_pig_modifier = 0.7f;
    protected long ticks_to_explode = 80;
    protected double hitbox_radius = 2;
    protected float pig_speed = 1.2f;

    public BabyBaconBombs() {
        super();
        this.name = "Baby Bacon Bombs";
        this.description = new String[] {
                ChatColor.RESET + "Give birth to a baby pig, giving",
                ChatColor.RESET + "yourself a boost forwards. ",
                ChatColor.RESET + "",
                ChatColor.RESET + "Your baby pig will run to annoy",
                ChatColor.RESET + "nearby enemies, exploding on them.",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        Kit kit = KitManager.getPlayerKit(owner);
        if(kit != null) {
            NetherPig netherPig = kit.getAttributeByClass(NetherPig.class);
            this.expUsed = (netherPig.active ? base_energy_cost * nether_pig_modifier : base_energy_cost);
        }
        if(owner.getExp() < this.expUsed) {
            Utils.sendAttributeMessage("Not enough Energy to use", name, owner, ServerMessageType.ENERGY);
            return;
        }
        if (System.currentTimeMillis() - last_time_used < cooldown_time_ms) {
            return;
        }
        checkAndActivate();
    }

    public void activate() {
        last_time_used = System.currentTimeMillis();
        VelocityUtil.setVelocity(owner, owner.getLocation().getDirection(), 0.8, true, 1.2, 0, 1, true);
        owner.getWorld().playSound(owner.getLocation(), Sound.PIG_IDLE, 2f, 0.75f);
        Pig pig = owner.getWorld().spawn(owner.getLocation(), Pig.class);
        pig.setHealth(5);
        pig.setVelocity(new Vector(0, -0.4, 0));
        pig.setBaby();
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if(owner == null) {
                    cancel();
                    return;
                }
                if(!pig.isValid() || pig.getTicksLived() > ticks_to_explode) {
                    explodePig(pig);
                    cancel();
                    return;
                }
                List<Player> targets = Utils.getNearby(pig.getLocation(), 20);
                for(Player target : targets) {
                    if(target.equals(owner)) {
                        continue;
                    }
                    if(!DamageUtil.canDamage(target, owner)) {
                        continue;
                    }
                    Utils.creatureMove(pig, target.getLocation(), pig_speed);
                    if(pig.getLocation().distance(target.getLocation()) < hitbox_radius) {
                        explodePig(pig);
                        cancel();
                        return;
                    }
                }
            }
        };
        runnable.runTaskTimer(plugin, 0L, 0L);
    }

    public void explodePig(Pig pig) {
        Utils.playParticle(EnumParticle.EXPLOSION_LARGE, pig.getLocation().add(0, 0.5, 0),
                0, 0, 0, 0, 1, 96, pig.getWorld().getPlayers());
        pig.getWorld().playSound(pig.getLocation(), Sound.EXPLODE, 0.6f, 2f);
        pig.getWorld().playSound(pig.getLocation(), Sound.PIG_DEATH, 1f, 2f);
        HashMap<LivingEntity, Double> targets = Utils.getInRadius(pig.getLocation(), damage_radius);
        for(LivingEntity livingEntity : targets.keySet()) {
            if(livingEntity.equals(owner)) {
                continue;
            }
            SmashDamageEvent smashDamageEvent = new SmashDamageEvent(livingEntity, owner, damage);
            smashDamageEvent.multiplyKnockback(0);
            smashDamageEvent.setIgnoreDamageDelay(true);
            smashDamageEvent.setReason(name);
            smashDamageEvent.callEvent();
        }
        pig.remove();
    }

}