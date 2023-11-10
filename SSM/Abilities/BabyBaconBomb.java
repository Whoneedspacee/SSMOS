package SSM.Abilities;

import SSM.GameManagers.DisguiseManager;
import SSM.GameManagers.Disguises.NetherPigDisguise;
import SSM.GameManagers.OwnerEvents.OwnerRightClickEvent;
import SSM.Projectiles.BabyBaconBombProjectile;
import SSM.Projectiles.BaconProjectile;
import SSM.Projectiles.IronHookProjectile;
import SSM.Projectiles.SulphurProjectile;
import SSM.Utilities.Utils;
import SSM.Utilities.VelocityUtil;
import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPig;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Pig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class BabyBaconBomb extends Ability implements OwnerRightClickEvent {

    private float energy_to_use = 0.35f;
    private long lastTimeUsed;
    private int pig_task;

    public BabyBaconBomb() {
        super();
        this.name = "Baby Bacon Bomb";
        this.description = new String[] {
                ChatColor.RESET + "Give birth to a baby pig, giving",
                ChatColor.RESET + "yourself a boost forwards. ",
                ChatColor.RESET + "",
                ChatColor.RESET + "Your baby pig will run to annoy",
                ChatColor.RESET + "nearby enemies, exploding on them.",
        };
    }

    public void onOwnerRightClick(PlayerInteractEvent e) {
        checkAndActivate();
    }

    public void activate() {
        float energy = DisguiseManager.getDisguise(owner) instanceof NetherPigDisguise ? energy_to_use * 0.7f : energy_to_use;

        if (owner.getExp() < energy) return;
        if (System.currentTimeMillis() <= lastTimeUsed + 100) return;
        owner.setExp(owner.getExp()-energy);
        lastTimeUsed = System.currentTimeMillis();
        VelocityUtil.setVelocity(owner, owner.getLocation().getDirection(), 0.8, true, 1.2, 0, 1, true);
        owner.getWorld().playSound(owner.getLocation(), Sound.PIG_IDLE, 2f, 0.75f);
        BabyBaconBombProjectile projectile = new BabyBaconBombProjectile(owner, name);
        projectile.launchProjectile();


    }



}