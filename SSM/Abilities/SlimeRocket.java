package SSM.Abilities;

import SSM.GameManagers.CooldownManager;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Utilities.ServerMessageType;
import SSM.Utilities.Utils;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Slime;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class SlimeRocket extends Ability implements OwnerRightClickEvent {

    private int task = -1;
    private int task_clear_slime = -1;

    public SlimeRocket() {
        super();
        this.name = "Slime Rocket";
        this.cooldownTime = 6;
        this.usage = AbilityUsage.BLOCKING;
        this.useMessage = "You are charging";
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        long activation_ms = System.currentTimeMillis();
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () ->
        {
            if (!owner.isBlocking()) {
                // Fire Slime
                Bukkit.getScheduler().cancelTask(task);
                fireRocket();
                return;
            }
            long elapsed_ms = CooldownManager.getInstance().getTimeElapsedFor(this, owner);
            double elapsed_sound = Math.min(3, (double) (System.currentTimeMillis() - activation_ms) / 1000d);
            if (owner.getExp() < 0.1 || elapsed_ms >= 5000) {
                // Fire Slime
                Bukkit.getScheduler().cancelTask(task);
                fireRocket();
                return;
            }
            if (elapsed_ms < 3000) {
                owner.setExp((float) Math.max(0, owner.getExp() - 0.01f));
            }
            owner.getWorld().playSound(owner.getLocation(), Sound.SLIME_WALK, 0.5f, (float) (0.5 + 1.5 * (elapsed_sound / 3d)));
            Utils.playParticle(EnumParticle.SLIME, owner.getLocation().add(0, 1, 0),
                    (float) (elapsed_sound / 6d), (float) (elapsed_sound / 6d), (float) (elapsed_sound / 6d), 0, (int) (elapsed_sound * 5),
                    96, null);
        }, 0L, 0L);
    }

    public void fireRocket() {
        Utils.sendServerMessageToPlayer("§7You released §a" + name + "§7.",
                owner, ServerMessageType.SKILL);
        long startTime = CooldownManager.getInstance().getStartTimeFor(this, owner);
        double charge = Math.min(3, (double) (System.currentTimeMillis() - startTime) / 1000d);
        Slime slime = owner.getWorld().spawn(owner.getEyeLocation(), Slime.class);
        slime.setSize(Math.max(1, (int) charge));
        slime.setMaxHealth(5 + charge * 7);
        slime.setHealth(slime.getMaxHealth());
        slime.setMetadata("Slime Owner", new FixedMetadataValue(plugin, owner.getUniqueId().toString()));
        //slime.leaveVehicle();
        //owner.eject();
        //SlimeProjectile projectile = new SlimeProjectile(plugin, name, slime, charge);
        //projectile.setFirer(owner);
        //projectile.launchProjectile();
        task_clear_slime = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if (slime.getTicksLived() > 120) {
                    slime.setTicksLived(1);

                    int entity_amount = 6 + 6 * slime.getSize();
                    List<Entity> slimeItems = new ArrayList<Entity>();
                    for (int i = 0; i < entity_amount; i++) {
                        Item item = slime.getLocation().getWorld().dropItem(slime.getLocation(), new ItemStack(Material.SLIME_BALL, 1));
                        double velocity = 0.2 + 0.1 * slime.getSize();
                        item.setVelocity(new Vector((Math.random() - 0.5) * velocity, Math.random(), (Math.random() - 0.5) * velocity));
                        item.setPickupDelay(999999);
                        slimeItems.add(item);
                    }

                    Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                        @Override
                        public void run() {
                            for (Entity ent : slimeItems) {
                                ent.remove();
                            }
                        }
                    }, 15L);

                    if (slime.getSize() <= 1)
                        slime.remove();
                    else
                        slime.setSize(slime.getSize() - 1);
                }
            }
        }, 0, 20L);
    }

}
