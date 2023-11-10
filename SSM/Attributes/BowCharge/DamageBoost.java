package SSM.Attributes.BowCharge;

import SSM.Events.SmashDamageEvent;
import SSM.GameManagers.OwnerEvents.OwnerDealSmashDamageEvent;
import SSM.Utilities.Utils;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DamageBoost extends BowCharge implements OwnerDealSmashDamageEvent {

    private Arrow firedArrow;
    private List<Arrow> fired_arrows = new ArrayList<Arrow>();

    public DamageBoost(double delay, double rate, int maxCharge) {
        super(delay, rate, maxCharge);
        this.name = "Corrupted Arrow";
        this.usage = AbilityUsage.CHARGE_BOW;
        this.description = new String[]{
                ChatColor.RESET + "Charge your arrows to corrupt them,",
                ChatColor.RESET + "adding up to an additional 6 damage.",
        };
        this.runTaskTimer(plugin, 0L, 0L);
    }

    @Override
    public void run() {
        if(owner == null || !owner.isValid()) {
            this.cancel();
            return;
        }
        for(Iterator<Arrow> arrowIterator = fired_arrows.iterator(); arrowIterator.hasNext();) {
            Arrow arrow = arrowIterator.next();
            if(arrow.isDead() || !arrow.isValid() || arrow.isOnGround() || arrow.getTicksLived() > 120) {
                arrowIterator.remove();
                continue;
            }
            Utils.playParticle(EnumParticle.REDSTONE, arrow.getLocation(),
                    0, 0, 0, 0, 1, 96, arrow.getWorld().getPlayers());
        }
    }

    @Override
    public void firedBow(Arrow arrow) {
        if(charge > 0) {
            fired_arrows.add(arrow);
        }
        firedArrow = arrow;
        checkAndActivate();
    }

    public void activate() {
        firedArrow.setMetadata("Damage Boost", new FixedMetadataValue(plugin, charge));
    }

    @Override
    public void onOwnerDealSmashDamageEvent(SmashDamageEvent e) {
        if(!(e.getProjectile() instanceof Arrow)) {
            return;
        }
        Arrow arrow = (Arrow) e.getProjectile();
        List<MetadataValue> data = arrow.getMetadata("Damage Boost");
        if (data.size() <= 0) {
            return;
        }
        int charge = data.get(0).asInt();
        if(charge <= 0) {
            return;
        }
        e.setDamage(e.getDamage() + charge * 0.9);
        fired_arrows.remove(arrow);
        e.setReason(name);
        e.setReasonColor(ChatColor.GREEN);
    }

}

