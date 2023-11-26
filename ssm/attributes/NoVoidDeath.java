package ssm.attributes;

import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ssm.events.SmashDamageEvent;
import ssm.managers.GameManager;
import ssm.managers.gamemodes.SmashGamemode;
import ssm.managers.ownerevents.OwnerTakeSmashDamageEvent;
import ssm.managers.smashserver.SmashServer;

public class NoVoidDeath extends Attribute implements OwnerTakeSmashDamageEvent {

    protected double damage_punishment;
    protected boolean ignore_armor;

    public NoVoidDeath(double damage_punishment, boolean ignore_armor) {
        super();
        this.name = "No Void Death";
        this.damage_punishment = damage_punishment;
        this.ignore_armor = ignore_armor;
    }

    public void activate() {
        return;
    }

    @Override
    public void onOwnerTakeSmashDamageEvent(SmashDamageEvent e) {
        if(e.getDamageCause() != EntityDamageEvent.DamageCause.VOID) {
            return;
        }
        e.setDamage(damage_punishment);
        e.setIgnoreArmor(ignore_armor);
        SmashServer server = GameManager.getPlayerServer(owner);
        if(server != null) {
            SmashGamemode gamemode = server.getCurrentGamemode();
            if(gamemode != null) {
                owner.teleport(gamemode.getRandomRespawnPoint(owner));
                return;
            }
        }
        owner.teleport(owner.getWorld().getSpawnLocation());
    }
}
