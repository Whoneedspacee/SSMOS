package ssm.abilities.ssmos;

import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import ssm.abilities.Ability;
import ssm.attributes.Attribute;
import ssm.events.SmashDamageEvent;
import ssm.managers.ownerevents.OwnerDealSmashDamageEvent;
import ssm.managers.ownerevents.OwnerRightClickEvent;
import ssm.projectiles.original.SulphurProjectile;

public class TheBestDefense extends Attribute implements OwnerDealSmashDamageEvent {

    public TheBestDefense() {
        super();
        this.name = "The Best Defense...";
        this.description = new String[] {
                ChatColor.RESET + "Landing abilities will temporarily",
                ChatColor.RESET + "grant yellow health that quickly goes away",

        };
        task = this.runTaskTimer(plugin, 0L, 15L);
    }

    @Override
    public void run() {
        EntityLiving el = ((CraftPlayer)owner).getHandle();
        if (el.getAbsorptionHearts() == 0){
            return;
        }
        el.setAbsorptionHearts(el.getAbsorptionHearts()-1);
    }

    public void activate() {

    }

    @Override
    public void onOwnerDealSmashDamageEvent(SmashDamageEvent e) {
        if (e.isCancelled()){
            return;
        }
        if (e.getDamageCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)){
            return;
        }
//        double healthGain = Math.floor(e.getDamage()/1.5);
        double healthGain = Math.floor(e.getDamage()*3);
        healthGain = healthGain == 0 ? 1 : healthGain;

        EntityLiving el = ((CraftPlayer)owner).getHandle();
        el.setAbsorptionHearts((float) (el.getAbsorptionHearts()+healthGain));
    }
}