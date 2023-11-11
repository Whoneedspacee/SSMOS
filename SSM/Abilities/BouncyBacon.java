package SSM.Abilities;

import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.NetherPigDisguise;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Projectiles.BaconProjectile;
import SSM.Projectiles.IronHookProjectile;
import SSM.Projectiles.SulphurProjectile;
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

    private float energy_to_use = 0.225f;
    private long lastTimeUsed;
    private List<Entity> bouncyBacons = new ArrayList<>();// eh system could be memory leak or something

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
        checkAndActivate();
    }

    public void activate() {
        float energy = DisguiseManager.getDisguise(owner) instanceof NetherPigDisguise ? energy_to_use * 0.7f : energy_to_use;

        if (owner.getExp() < energy) return;
        if (System.currentTimeMillis() <= lastTimeUsed + 100) return;
        BaconProjectile projectile = new BaconProjectile(owner, name);
        projectile.launchProjectile();
        owner.setExp(owner.getExp()-energy);
        lastTimeUsed = System.currentTimeMillis();
        bouncyBacons.add(projectile.getProjectileEntity());
    }

    @EventHandler
    public void bouncyPickup(PlayerPickupItemEvent event){
        if (!event.getPlayer().equals(owner)) return;
        if (!bouncyBacons.contains(event.getItem())) return;
        event.setCancelled(true);

        Item item = event.getItem();
        owner.getWorld().playSound(event.getPlayer().getLocation(), Sound.EAT, 2f, 1f);
        owner.setExp(Math.min(0.999f, owner.getExp()+0.05f));
        if (item.getItemStack().getType().equals(Material.GRILLED_PORK)){
            owner.setHealth(Math.min(owner.getMaxHealth(), owner.getHealth()+1));
            Utils.playParticle(EnumParticle.HEART, event.getPlayer().getLocation().add(0, 0.5, 0), 0.2f, 0.2f, 0.2f, 0, 4, 96, item.getWorld().getPlayers() );
        }
        item.remove();
    }



}