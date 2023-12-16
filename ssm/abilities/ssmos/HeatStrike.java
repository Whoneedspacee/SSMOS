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
import ssm.attributes.Attribute;
import ssm.events.SmashDamageEvent;
import ssm.managers.CooldownManager;
import ssm.managers.ownerevents.OwnerDealSmashDamageEvent;
import ssm.managers.ownerevents.OwnerDeathEvent;
import ssm.managers.ownerevents.OwnerRightClickEvent;
import ssm.projectiles.original.SulphurProjectile;
import ssm.utilities.LineParticle;
import ssm.utilities.Utils;
import ssm.utilities.VelocityUtil;

public class HeatStrike extends Attribute implements OwnerDealSmashDamageEvent {

    private LivingEntity target;
    private int stacks = 0;
    private int theta = 0;
    private int remove_task = -1;

    public HeatStrike() {
        super();
        this.name = "Soul Detach";
        task = this.runTaskTimer(plugin, 0L, 0L);
    }


    public void activate() {

    }

    @Override
    public void onOwnerDealSmashDamageEvent(SmashDamageEvent e) {
        if (e.isCancelled()) {
            return;
        }
        if (!e.getDamageCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)){
            return;
        }
        target = e.getDamagee();
        if (stacks == 3){
            e.setDamage(e.getDamage()+2);
            stacks = 0;
            e.getDamagee().setFireTicks(40);
            e.multiplyKnockback(2);
            return;
        }

        stacks = Math.min(stacks+1, 3);

        Bukkit.getScheduler().cancelTask(remove_task);
        remove_task = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                stacks = 0;
            }
        }, 100L);

    }

    @Override
    public void run() {
        if (target == null || stacks == 0) {
            return;
        }
        theta += 2;
        if (theta >= 360){
            theta = 0;
        }

        int theta_difference = 360 / stacks;

        double cos = Math.cos(Math.toRadians(theta)) * 1.2;
        double sin = Math.sin(Math.toRadians(theta)) * 1.2;

        Utils.playParticle(EnumParticle.FLAME, target.getLocation().add(cos, 1, sin),
                0.05f, 0f, 0.05f, 0, 1, 96, owner.getWorld().getPlayers());

        int counter = theta;
        for (int i = 0; i < stacks-1; i++){
            counter += theta_difference;
            double cos1 = Math.cos(Math.toRadians(counter)) * 1.2;
            double sin1 = Math.sin(Math.toRadians(counter)) * 1.2;
            Utils.playParticle(EnumParticle.FLAME, target.getLocation().add(cos1, 1, sin1),
                    0.05f, 0f, 0.05f, 0, 1, 96, owner.getWorld().getPlayers());
        }
    }
}
