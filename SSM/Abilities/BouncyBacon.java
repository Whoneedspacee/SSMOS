package SSM.Abilities;

import SSM.Attributes.NetherPig;
import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.NetherPigDisguise;
import SSM.GameManagers.KitManager;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Kits.Kit;
import SSM.Projectiles.BaconProjectile;
import SSM.Projectiles.IronHookProjectile;
import SSM.Projectiles.SulphurProjectile;
import SSM.Utilities.ServerMessageType;
import SSM.Utilities.Utils;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.ArrayList;
import java.util.List;

public class BouncyBacon extends Ability implements OwnerRightClickEvent {

    private static List<Entity> bouncy_bacons = new ArrayList<>();
    private long last_time_used = 0;
    protected long cooldown_time_ms = 100;
    protected float base_energy_cost = 0.225f;
    protected float nether_pig_modifier = 0.7f;
    protected float energy_recharge = 0.05f;
    protected double health_regain = 1;

    public BouncyBacon() {
        super();
        this.name = "Bouncy Bacon";
        this.description = new String[] {
                ChatColor.RESET + "Bouncy Bacon launches a piece of bacon,",
                ChatColor.RESET + "dealing damage and knockback to enemies.",
                ChatColor.RESET + "",
                ChatColor.RESET + "Eat the bacon to restore some Energy.",
                ChatColor.RESET + "Bacon that hits an enemy will restore Health.",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        Kit kit = KitManager.getPlayerKit(owner);
        if(kit != null) {
            NetherPig netherPig = (NetherPig) kit.getAttributeByClass(NetherPig.class);
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
        BaconProjectile projectile = new BaconProjectile(owner, name);
        projectile.launchProjectile();
        last_time_used = System.currentTimeMillis();
        bouncy_bacons.add(projectile.getProjectileEntity());
    }

    @EventHandler
    public void bouncyPickup(PlayerPickupItemEvent event){
        if (!event.getPlayer().equals(owner)) {
            return;
        }
        if (!bouncy_bacons.contains(event.getItem())) {
            return;
        }
        event.setCancelled(true);
        Item item = event.getItem();
        owner.getWorld().playSound(event.getPlayer().getLocation(), Sound.EAT, 2f, 1f);
        owner.setExp(Math.min(0.999f, owner.getExp() + energy_recharge));
        if (item.getItemStack().getType().equals(Material.GRILLED_PORK)){
            owner.setHealth(Math.min(owner.getMaxHealth(), owner.getHealth() + health_regain));
            Utils.playParticle(EnumParticle.HEART, event.getPlayer().getLocation().add(0, 0.5, 0),
                    0.2f, 0.2f, 0.2f, 0, 4, 96, owner.getWorld().getPlayers() );
        }
        item.remove();
    }

}