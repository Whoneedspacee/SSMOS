package ssm.abilities.ssmos;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import ssm.Main;
import ssm.abilities.Ability;
import ssm.events.SmashDamageEvent;
import ssm.managers.CooldownManager;
import ssm.managers.ownerevents.OwnerDealSmashDamageEvent;
import ssm.managers.ownerevents.OwnerDeathEvent;
import ssm.managers.ownerevents.OwnerRightClickEvent;
import ssm.projectiles.original.SulphurProjectile;
import ssm.utilities.LineParticle;
import ssm.utilities.Utils;
import ssm.utilities.VelocityUtil;

public class SoulDetach extends Ability implements OwnerRightClickEvent, OwnerDealSmashDamageEvent, OwnerDeathEvent {

    private PigZombie soul;
    private long start_time = 0;
    private LivingEntity lastTarget;
    private double damage = 0;
    private final static int DURATION = 4000;
    private int soul_task = -1;

    public SoulDetach() {
        super();
        this.name = "Soul Detach";
        this.cooldownTime = 10;
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        if (soul == null){
            checkAndActivate();
            return;
        }
        endAbility(false);
    }

    private void endAbility(boolean onDeath){
        Bukkit.getScheduler().cancelTask(soul_task);
        if (!onDeath && soul != null){
            owner.teleport(soul);
        }
        if (lastTarget != null && !lastTarget.equals(soul)){
            SmashDamageEvent smashDamageEvent = new SmashDamageEvent(lastTarget, owner, damage*0.25);
            smashDamageEvent.multiplyKnockback(0);
            smashDamageEvent.setIgnoreDamageDelay(true);
            smashDamageEvent.setReason(name);
            smashDamageEvent.callEvent();
            // maybe make it pull?
            // door break zombie sound
        }
        if (soul != null){
            soul.remove();
        }
        damage = 0;
        lastTarget = null;
        soul = null;
    }

    public void activate() {
        soul = (PigZombie) owner.getWorld().spawnEntity(owner.getLocation(), EntityType.PIG_ZOMBIE);
        soul.setBaby(false);
        disableAI(soul);
        start_time = System.currentTimeMillis();
        VelocityUtil.setVelocity(owner, 1.35, 0.2, 1.4, true);
        soul_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() > start_time + DURATION){
                    endAbility(false);
                    return;
                }
                soul.setVelocity(new Vector(0, 0, 0));
                double percentage_done = ((double) CooldownManager.getInstance().getTimeElapsedFor(SoulDetach.this, owner) / DURATION);
                double angle = 360 * percentage_done;
                for (int theta = 1; theta <= angle; theta += 5){
                    double cos = Math.cos(Math.toRadians(theta)) * 1.5;
                    double sin = Math.sin(Math.toRadians(theta)) * 1.5;
//                    Utils.playParticle(EnumParticle.FLAME, owner.getLocation().add(cos, 0, sin),
//                            0f, 0f, 0f, 0, 1, 96, owner.getWorld().getPlayers());
                    Utils.playParticle(EnumParticle.FLAME, soul.getLocation().add(cos, 0, sin),
                            0f, 0f, 0f, 0, 1, 96, owner.getWorld().getPlayers());
                }

            }
        }, 0L, 0L);

    }

    @Override
    public void onOwnerDealSmashDamageEvent(SmashDamageEvent e) {
        if (soul == null){
            return;
        }
        if (e.isCancelled()){
            return;
        }
        damage += e.getDamage();
        lastTarget = e.getDamagee();
    }

    @EventHandler
    public void dmg(SmashDamageEvent e){
        if(e.isCancelled()) {
            return;
        }
        if(e.getDamageCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }
        if (!e.getDamagee().equals(soul)){
            return;
        }
        if (e.getDamager().equals(owner)){
            return;
        }
        e.setDamagee(owner);
        e.multiplyKnockback(0);
        e.setDamage(e.getDamage()*0.75);
    }

    private void disableAI(Entity entity) {
        net.minecraft.server.v1_8_R3.Entity nmsEnt = ((CraftEntity) entity).getHandle();
        NBTTagCompound tag = nmsEnt.getNBTTag();

        if(tag == null) {
            tag = new NBTTagCompound();
        }

        nmsEnt.c(tag);
        tag.setInt("NoAI", 1);
        nmsEnt.f(tag);
    }

    @Override
    public void onOwnerDeathEvent() {;
        endAbility(true);
    }
}