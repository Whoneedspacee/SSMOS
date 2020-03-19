package me.SirInHueman.SSM;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class SSM extends JavaPlugin implements Listener {
    SulphurBomb SB = new SulphurBomb();
    IronHook IH = new IronHook();



    @Override
    public void onEnable(){
        getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                if (SB.ent.getCustomName().equalsIgnoreCase("Sulphur Bomb")) {
                    List<Entity> ok = SB.ent.getNearbyEntities(0.5, 0.5, 0.5);
                    if (!SB.ent.isDead() && SB.ent.isOnGround()) {
                        SB.ent.remove();
                        ok.clear();
                    }
                    if (ok.size() >= 1 && ok.get(0) == SB.name) {

                    } else if (!SB.ent.isDead() && !SB.ent.isOnGround()) {
                        LivingEntity target = (LivingEntity) ok.get(0);
                        target.damage(6.0);
                        Vector a = SB.name.getLocation().toVector();
                        Vector b = target.getLocation().toVector();
                        Vector pre = b.subtract(a);
                        Vector velocity = pre.normalize().multiply(1.8);
                        target.setVelocity(new Vector(velocity.getX(), 0.5, velocity.getZ()));
                        SB.ent.remove();
                        ok.clear();
                    } // This is Sulphur Bomb ^^^
                }
            }
        }, 0L, 1L);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run(){
                if (IH.ent.getCustomName().equalsIgnoreCase("Iron Hook")) {
                    if (!IH.ent.isDead() && IH.ent.isOnGround()) {
                        IH.ent.remove();
                    }
                    List<Entity> ok = IH.ent.getNearbyEntities(0.46, 0.46, 0.46);
                    if (ok.size() >= 1 && ok.get(0) == IH.name) {
                    } else if (!IH.ent.isDead() && !IH.ent.isOnGround()) {
                        LivingEntity target = (LivingEntity) ok.get(0);
                        target.damage(5.0);
                        Vector a = IH.name.getLocation().toVector();
                        Vector b = target.getLocation().toVector();
                        Vector pre = b.subtract(a);
                        Vector velocity = pre.normalize().multiply(-2);
                        target.setVelocity(new Vector(velocity.getX(), velocity.getY() + 0.5, velocity.getZ()));
                        IH.ent.remove();
                        ok.clear();
                    }
                }
            }

        }, 0L, 1L);





    }
    @Override
    public void onDisable(){
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("kit")){
            Player player = sender.getServer().getPlayer(args[0]);
            if (args[1].equalsIgnoreCase("Skeleton")){
                KitSkeleton.Skeleton(player);
            } else if (args[1].equalsIgnoreCase("Iron") && args[2].equalsIgnoreCase("Golem")){
                KitIronGolem.IronGolem(player);
            } else if (args[1].equalsIgnoreCase("Slime")){
                KitSlime.Slime(player);
            } else if (args[1].equalsIgnoreCase("Spider")){
                KitSpider.Spider(player);
            } else if (args[1].equalsIgnoreCase("Creeper")){
                KitCreeper.Creeper(player);
            }
            return true;


        }
        return false;
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK){
            if (player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("Roped Arrow")){
                RopedArrow.ability(player);
            }

        }
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
            if (player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("Fissure")  && e.getHand().toString().equalsIgnoreCase("Hand")){
                Fissure.ability(player);
            }
            if (player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("Seismic Slam")  && e.getHand().toString().equalsIgnoreCase("Hand")){
                SeismicSlam.ability(player);
            }
            if (player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("Sulphur Bomb" )  && e.getHand().toString().equalsIgnoreCase("Hand")){
                SB.ability(player);
            }
            if (player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("Iron Hook") && e.getHand().toString().equalsIgnoreCase("Hand")){
                IH.ability(player);
            }
        }

    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        Player player = e.getPlayer();
        if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.GOLD_BLOCK){
            Double x = player.getLocation().getDirection().getX() * 1.2;
            Double z = player.getLocation().getDirection().getZ() * 1.2;
            player.setVelocity(new Vector(x, 1.2, z));
        }
        if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.WATER || player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.LAVA){
            player.setHealth(0.0);
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();
        String name = player.getDisplayName();
        e.setQuitMessage(ChatColor.YELLOW + name+" has fucking rage quit, what a fucking bitch LOL");

    }
    @EventHandler
    public void onProjectile(ProjectileHitEvent e){
        if (e.getEntity().getCustomName().equalsIgnoreCase("Roped Arrow")){
            Player player = (Player)e.getEntity().getShooter();
            if (player.getInventory().contains(Material.ARROW, 1)){
                Projectile fuck = e.getEntity();
                RopedArrow.velocity(player, fuck);
            }
        }
        if (e.getEntity().getCustomName().equalsIgnoreCase("Sulphur Bomb")){
            Player x = (Player)e.getEntity().getShooter();
            x.sendMessage("???");

        }
    }
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e){
        Player player = e.getPlayer();
        Entity NPC = e.getRightClicked();
        if (NPC.getCustomName().equalsIgnoreCase("Alchemist")){
            Random r = new Random();
            int potion = r.nextInt((10 - 1) + 1) + 1;
            if (potion == 1){
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 2));
                // 20 * 15 = 300 (Ticks). Amplfier 2 goes to 3.
            }else if (potion == 2){
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 300, 2));
            }else if (potion == 3){
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 300, 2));
            }else if (potion == 4){
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 2));
            }else if (potion == 5){
                player.addPotionEffect(new PotionEffect(PotionEffectType.HEAL, 20, 2));
            }else if (potion == 6){
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 300, 2));
            }else if (potion == 7){
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 300, 2));
            }else if (potion == 8){
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 300, 2));
            }else if (potion == 9){
                player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20, 20));
            }else if (potion == 10){
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20, 20));
            }
           NPC.remove();






        }

    }
    }












