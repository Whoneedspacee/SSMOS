package ssm.attributes;

import ssm.gamemanagers.ownerevents.OwnerToggleSneakEvent;
import ssm.utilities.Utils;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class Teleport extends Attribute implements OwnerToggleSneakEvent {

    private int charge_task = -1;
    private Block target = null;
    private float charge = 0;
    private float charge_rate = 0.015f;

    public Teleport() {
        super();
        this.name = "Teleport";
        this.cooldownTime = 5;
        this.usage = AbilityUsage.CROUCH;
        this.description = new String[] {
                ChatColor.RESET + "While crouching, you charge up a teleport",
                ChatColor.RESET + "appearing at a target block.",
                ChatColor.RESET + "",
                ChatColor.RESET + "You cannot pass through blocks.",
                ChatColor.RESET + "",
        };
    }

    public void activate() {
        Utils.sendTitleMessage(owner, "", ChatColor.GREEN + "Teleported", 0, 10, 10);
        while (target.getRelative(BlockFace.UP).getType() != Material.AIR) {
            target = target.getRelative(BlockFace.UP);
        }
        owner.playSound(owner.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 0.5f);
        owner.teleport(target.getLocation().add(0.5, 1, 0.5).setDirection(owner.getLocation().getDirection()));
        owner.playSound(owner.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 0.5f);
        Utils.playParticle(EnumParticle.SPELL_WITCH, owner.getLocation().add(0, 1, 0),
                1f, 1f, 1f, 0.1f, 100, 96, owner.getWorld().getPlayers());
    }

    @Override
    public void onOwnerToggleSneak(PlayerToggleSneakEvent e) {
        if(!check()) {
            return;
        }
        target = null;
        if(Bukkit.getScheduler().isQueued(charge_task) || Bukkit.getScheduler().isCurrentlyRunning(charge_task)) {
            Bukkit.getScheduler().cancelTask(charge_task);
        }
        charge_task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(owner == null || !owner.isSneaking()) {
                    Bukkit.getScheduler().cancelTask(charge_task);
                    return;
                }
                Block block = Utils.getTargetBlock(owner, 100);
                if(target == null) {
                    target = block;
                    charge = 0;
                }
                if(block == null || block.getType() == Material.AIR) {
                    target = null;
                    return;
                }
                if(!target.equals(block)) {
                    target = block;
                    charge = 0;
                    return;
                }
                charge += charge_rate;
                Utils.sendTitleMessage(owner, "", Utils.progressString(charge), 0, 10, 10);
                if(charge < 1) {
                    owner.playSound(owner.getLocation(), Sound.ENDERMAN_TELEPORT, 1f, 1f + charge);
                    Utils.playParticle(EnumParticle.SPELL_WITCH, owner.getLocation().add(0, 1, 0),
                            1f, 1f, 1f, 0.05f, 10, 96, owner.getWorld().getPlayers());
                    return;
                }
                Bukkit.getScheduler().cancelTask(charge_task);
                checkAndActivate();
            }
        }, 0L, 0L);
    }

}
