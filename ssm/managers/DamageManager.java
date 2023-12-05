package ssm.managers;

import org.bukkit.entity.Item;
import ssm.attributes.Hunger;
import ssm.events.SmashDamageEvent;
import ssm.managers.disguises.Disguise;
import ssm.kits.Kit;
import ssm.Main;
import ssm.managers.smashserver.SmashServer;
import ssm.utilities.DamageUtil;
import ssm.utilities.Utils;
import ssm.utilities.VelocityUtil;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import net.minecraft.server.v1_8_R3.*;
import net.minecraft.server.v1_8_R3.Statistic;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftFallingSand;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DamageManager implements Listener {

    private static DamageManager ourInstance;
    private JavaPlugin plugin = Main.getInstance();
    private static List<SmashDamageEvent> damage_record = new ArrayList<>();
    public static HashMap<org.bukkit.entity.Entity, Integer> invincible_mobs = new HashMap<org.bukkit.entity.Entity, Integer>();

    public DamageManager() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        ourInstance = this;
    }

    // Disable mob combustion in sunlight
    @EventHandler
    public void onCombust(EntityCombustEvent e) {
        if(e instanceof EntityCombustByBlockEvent || e instanceof EntityCombustByEntityEvent) {
            return;
        }
        e.setCancelled(true);
    }

    // We will handle fast blocking in this way, the method
    // Other servers have used seems to be jar patches
    // Modify Entity Patch, EntityFallingBlock damageEntity
    // Other parts from EntityHuman.class attack code
    @EventHandler
    public void preAttack(PrePlayerAttackEntityEvent e) {
        if(!(e.getAttacked() instanceof FallingBlock)) {
            return;
        }
        e.setCancelled(true);
        EntityPlayer player = ((CraftPlayer) e.getPlayer()).getHandle();
        EntityFallingBlock entityFallingBlock = ((CraftFallingSand) (e.getAttacked())).getHandle();
        // EntityHuman.class attack code starts here, code is edited to ignore bits that would not call
        if(!(entityFallingBlock.aD() && !entityFallingBlock.l(player))) {
            return;
        }
        float f = (float)player.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue();
        byte b0 = 0;
        float f1 = 0.0F;
        f1 = EnchantmentManager.a(player.bA(), EnumMonsterType.UNDEFINED);
        int i = b0 + EnchantmentManager.a(player);
        if(player.isSprinting()) {
            ++i;
        }
        if (f > 0.0F || f1 > 0.0F) {
            boolean flag = false;
            if (flag && f > 0.0F) {
                f *= 1.5F;
            }

            f += f1;
            boolean flag1 = false;
            int j = EnchantmentManager.getFireAspectEnchantmentLevel(player);

            double d0 = entityFallingBlock.motX;
            double d1 = entityFallingBlock.motY;
            double d2 = entityFallingBlock.motZ;
            // Copy this in to replace what the patch did instead of calling the normal damage
            CraftEventFactory.handleNonLivingEntityDamageEvent(entityFallingBlock, DamageSource.playerAttack(player), f);
            // This is the return from the patch
            boolean flag2 = true;
            if (flag2) {
                if (i > 0) {
                    entityFallingBlock.g((double)(-MathHelper.sin(player.yaw * 3.1415927F / 180.0F) * (float)i * 0.5F), 0.1, (double)(MathHelper.cos(player.yaw * 3.1415927F / 180.0F) * (float)i * 0.5F));
                    //Bukkit.broadcastMessage(String.format("X: %.1f, Y: %.1f, Z: %.1f", entityFallingBlock.motX, entityFallingBlock.motY, entityFallingBlock.motZ));
                    player.motX *= 0.6;
                    player.motZ *= 0.6;
                    // This sets it on the server only, so just don't do that
                    // This means this is probably wrong since I do remember it toggling the players sprinting
                    // Unfortunately I have no idea what to do here
                    // According to some clicking block toss didn't toggle sprint though so maybe it is fine
                    //player.setSprinting(false);
                }

                if (flag) {
                    player.b(entityFallingBlock);
                }

                if (f1 > 0.0F) {
                    player.c(entityFallingBlock);
                }

                if (f >= 18.0F) {
                    player.b((Statistic)AchievementList.F);
                }

                player.p(entityFallingBlock);

                EnchantmentManager.b(player, entityFallingBlock);
                ItemStack itemstack = player.bZ();
                Object object = entityFallingBlock;

                player.applyExhaustion(player.world.spigotConfig.combatExhaustion);
            } else if (flag1) {
                player.extinguish();
            }
        }
        //Bukkit.broadcastMessage("Velocity: " + String.format("%.2f, %.2f, %.2f", entityFallingBlock.motX, entityFallingBlock.motY, entityFallingBlock.motZ));
    }

    // Highest priority to get after all changes
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.isCancelled()) {
            Bukkit.broadcastMessage(ChatColor.RED + "Error: Cancelled EntityDamageEvent, please use SmashDamageEvent");
            return;
        }
        if(e.getEntity() instanceof Item) {
            e.setCancelled(true);
        }
        if (!(e.getEntity() instanceof LivingEntity)) {
            return;
        }
        LivingEntity damagee = (LivingEntity) e.getEntity();
        LivingEntity damager = null;
        Projectile projectile = null;
        ChatColor reason_color = ChatColor.GREEN;
        double knockback = 1;
        if (e instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent damageBy = (EntityDamageByEntityEvent) e;
            if (damageBy.getDamager() instanceof Projectile) {
                projectile = (Projectile) damageBy.getDamager();
                if (projectile.getShooter() instanceof LivingEntity) {
                    damager = (LivingEntity) projectile.getShooter();
                }
            } else if (damageBy.getDamager() instanceof LivingEntity) {
                damager = (LivingEntity) damageBy.getDamager();
            }
        }
        double damage = e.getDamage();
        // Consistent Arrow Damage
        if (projectile instanceof Arrow) {
            damage = projectile.getVelocity().length() * 3;
            reason_color = ChatColor.YELLOW;
        }
        // Consistent Melee Damage
        if (damager instanceof Player && e.getCause() == DamageCause.ENTITY_ATTACK) {
            Kit kit = KitManager.getPlayerKit((Player) damager);
            if (kit != null) {
                damage = kit.getMelee();
            }
        }
        boolean display_as_last_damage = true;
        if (e.getCause() == DamageCause.POISON) {
            if (e.getEntity().hasMetadata("Poison Damager")) {
                List<MetadataValue> values = e.getEntity().getMetadata("Poison Damager");
                if (values.size() > 0) {
                    // Set damager so they get put on the damage rate
                    damager = (Player) values.get(0).value();
                    knockback = 0;
                    display_as_last_damage = false;
                }
            }
        }
        SmashDamageEvent smashDamageEvent = new SmashDamageEvent(damagee, damager, damage);
        smashDamageEvent.multiplyKnockback(knockback);
        smashDamageEvent.setDamageCause(e.getCause());
        smashDamageEvent.setProjectile(projectile);
        smashDamageEvent.setDisplayAsLastDamage(display_as_last_damage);
        smashDamageEvent.setReasonColor(reason_color);
        smashDamageEvent.callEvent();
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void removeArrows(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Arrow)) {
            return;
        }
        Arrow arrow = (Arrow) e.getEntity();
        arrow.teleport(new Location(arrow.getWorld(), 0, 0, 0));
        arrow.remove();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void removeArrows(SmashDamageEvent e) {
        if (!(e.getProjectile() instanceof Arrow)) {
            return;
        }
        Arrow arrow = (Arrow) e.getProjectile();
        arrow.teleport(new Location(arrow.getWorld(), 0, 0, 0));
        arrow.remove();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void redirectSmashDamage(SmashDamageEvent e) {
        if(!DisguiseManager.redirect_damage.containsKey(e.getDamagee())) {
            return;
        }
        boolean old_cancelled = e.isCancelled();
        e.setCancelled(true);
        // Be careful not to double up damage on the redirected entity
        if(e.getDamageCause() == DamageCause.SUFFOCATION) {
            return;
        }
        if(e.getDamageCause() == DamageCause.STARVATION) {
            return;
        }
        if(e.getDamageCause() == DamageCause.LAVA) {
            return;
        }
        if(e.getDamageCause() == DamageCause.VOID) {
            return;
        }
        if(e.getDamageCause() == DamageCause.ENTITY_EXPLOSION) {
            return;
        }
        if(e.getDamageCause() == DamageCause.BLOCK_EXPLOSION) {
            return;
        }
        if(e.getDamageCause() == DamageCause.DROWNING) {
            return;
        }
        e.setCancelled(old_cancelled);
        e.setDamagee(DisguiseManager.redirect_damage.get(e.getDamagee()));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void cancelSmashDamage(SmashDamageEvent e) {
        if(invincible_mobs.containsKey(e.getDamagee())) {
            e.setCancelled(true);
            return;
        }
        if (!DamageUtil.canDamage(e.getDamagee(), e.getDamager())) {
            e.setCancelled(true);
            return;
        }
        if (e.getDamagee().getHealth() <= 0) {
            e.setCancelled(true);
            return;
        }
        if (e.getDamagee() != null && e.getDamagee() instanceof Player) {
            Player damagee = (Player) e.getDamagee();
            if (damagee.getGameMode() != GameMode.SURVIVAL && damagee.getGameMode() != GameMode.ADVENTURE) {
                e.setCancelled(true);
                return;
            }
            // Check if the damagee can be hit by the damager again
            if (!e.getIgnoreDamageDelay()) {
                if (!DamageUtil.getDamageRateTracker(damagee).canBeHurtBy(e.getDamager())) {
                    e.setCancelled(true);
                    return;
                }
            }
        }

        if (e.getDamager() != null && e.getDamager() instanceof Player) {
            Player damager = (Player) e.getDamager();
            if (damager.getGameMode() != GameMode.SURVIVAL && damager.getGameMode() != GameMode.ADVENTURE) {
                e.setCancelled(true);
                return;
            }
            // Check if the damager can hit the damagee again
            if (!e.getIgnoreDamageDelay())
                if (!DamageUtil.getDamageRateTracker(damager).canHurt(e.getDamagee())) {
                    e.setCancelled(true);
                    return;
                }
        }
    }

    @EventHandler
    public void borderKill(SmashDamageEvent e) {
        if (e.getDamageCause() != DamageCause.VOID) {
            return;
        }
        if (e.getIgnoreDamageDelay()) {
            return;
        }
        if (!(e.getDamagee() instanceof Player)) {
            return;
        }
        DamageUtil.borderKill((Player) e.getDamagee(), true);
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSmashDamage(SmashDamageEvent e) {
        if (e.isCancelled()) {
            return;
        }
        /*if(e.getDamager() instanceof Player) {
            Player player = (Player) e.getDamager();
            player.sendMessage("Damaged: " + e.getReason());
            player.sendMessage("Damage: " + e.getDamage());
        }*/
        LivingEntity damagee = e.getDamagee();
        LivingEntity damager = e.getDamager();
        double damage = e.getDamage();
        double knockbackMultiplier = e.getKnockbackMultiplier();
        boolean ignoreArmor = e.getIgnoreArmor();
        DamageCause cause = e.getDamageCause();
        Location origin = e.getKnockbackOrigin();
        String reason = e.getReason();
        Projectile projectile = e.getProjectile();
        if (damagee == null || damage <= 0) {
            e.setCancelled(true);
            return;
        }
        if (!DamageUtil.canDamage(damagee, damager)) {
            e.setCancelled(true);
            return;
        }
        double damageMultiplier = 1;
        double starting_health = damagee.getHealth();
        if (damagee instanceof Player) {
            Player player = (Player) damagee;
            if (KitManager.getPlayerKit((player)) != null) {
                damageMultiplier = Math.max(0, 1 - KitManager.getPlayerKit(player).getArmor() * 0.08f);
            }
        }
        if (ignoreArmor) {
            damageMultiplier = 1;
        }
        double previousHealth = damagee.getHealth();
        EntityLiving entityDamagee = ((CraftLivingEntity) damagee).getHandle();
        boolean died = false;
        double new_health = 20;
        double dealt = 0;
        if ((float) damagee.getNoDamageTicks() > (float) damagee.getMaximumNoDamageTicks() / 2.0F) {
            dealt = Math.max(damage - entityDamagee.lastDamage, 0) * damageMultiplier;
        } else {
            dealt = damage * damageMultiplier;
        }
        new_health = Math.max(damagee.getHealth() - dealt, 0);
        entityDamagee.lastDamage = (float) damage;
        // Avoid really killing the player
        if (new_health <= 0) {
            died = true;
        } else {
            damagee.setHealth(new_health);
        }
        storeDamageEvent(e);
        Disguise disguise = null;
        if(damagee instanceof Player) {
            disguise = DisguiseManager.disguises.get(damagee);
            if (disguise != null && disguise.getLiving() != null) {
                PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus((net.minecraft.server.v1_8_R3.Entity) disguise.getLiving(), (byte) 2);
                Utils.sendPacketToAllBut(disguise.getOwner(), packet);
            }
        }
        damagee.playEffect(EntityEffect.HURT);
        if (cause == DamageCause.ENTITY_ATTACK || cause == DamageCause.PROJECTILE) {
            if (damagee instanceof Player) {
                if (disguise != null) {
                    disguise.playDamageSound();
                } else {
                    DamageUtil.playDamageSound(damagee, damagee.getType());
                }
            } else {
                DamageUtil.playDamageSound(damagee, damagee.getType());
            }
        }
        if (damager instanceof Player && cause == DamageCause.PROJECTILE && projectile instanceof Arrow) {
            Player player = (Player) damager;
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.5f, 0.5f);
        }
        if (died && !(damagee instanceof Player)) {
            // Actually kill entities
            damagee.damage(100);
        }
        if (died && damagee instanceof Player) {
            Player player = (Player) damagee;
            SmashServer server = GameManager.getPlayerServer(player);
            if(server != null) {
                server.death(player);
            }
        }
        if (damager instanceof Player && cause != DamageCause.VOID) {
            Player player = (Player) damager;
            player.setLevel((int) damage);
        }
        if (knockbackMultiplier > 0 && (origin != null || damager != null || projectile != null)) {
            // Don't stack knockback
            VelocityUtil.setVelocity(damagee, new Vector(0, 0, 0));
            double knockback = Math.max(damage, 2);
            knockback = Math.log10(knockback);
            knockback *= knockbackMultiplier;

            if (damagee instanceof Player) {
                Player player = (Player) damagee;
                if (KitManager.getPlayerKit(player) != null) {
                    knockback *= KitManager.getPlayerKit(player).getKnockback();
                }
                knockback *= (1 + 0.1 * (damagee.getMaxHealth() - starting_health));
            }

            //Bukkit.broadcastMessage("HP: " + damagee.getHealth() + " Max: " + damagee.getMaxHealth());
            //Bukkit.broadcastMessage("Damage: " + damage);
            //Bukkit.broadcastMessage("Final KB: " + knockback);

            if (origin == null && damager != null) {
                origin = damager.getLocation();
            }

            Vector trajectory = null;
            if (origin != null) {
                trajectory = damagee.getLocation().toVector().subtract(origin.toVector()).setY(0).normalize();
                trajectory.multiply(0.6 * knockback);
                trajectory.setY(Math.abs(trajectory.getY()));
            }
            if (projectile != null) {
                trajectory = projectile.getVelocity();
                trajectory.setY(0);
                trajectory.multiply(0.37 * knockback / trajectory.length());
                trajectory.setY(0.06);
            }

            double vel = 0.2 + trajectory.length() * 0.8;

            VelocityUtil.setVelocity(damagee, trajectory, vel, false,
                    0, Math.abs(0.2 * knockback), 0.4 + (0.04 * knockback), true);
        }
        if (damagee != damager && damager instanceof Player && damagee instanceof Player) {
            Player player = (Player) damager;
            Kit kit = KitManager.getPlayerKit(player);
            if (kit != null) {
                Hunger hunger = kit.getAttributeByClass(Hunger.class);
                if (hunger != null) {
                    hunger.hungerRestore(damage);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void clearArrow(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Arrow) {
            if (e.getHitEntity() != null) {
                e.getEntity().remove();
                return;
            }
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    Arrow arrow = (Arrow) e.getEntity();
                    arrow.remove();
                }
            }, 300L);
        }
    }

    public static void storeDamageEvent(SmashDamageEvent e) {
        // Consider cloning these to avoid changing original event data
        // Condense Damage Records with same variable values except damage and time
        /*for (int i = 0; i < damage_record.size(); i++) {
            SmashDamageEvent check = damage_record.get(i);
            if (check == null) {
                continue;
            }
            // Ignore old records
            if ((System.currentTimeMillis() - check.getDamageTimeMs()) > 15000) {
                continue;
            }
            if (e.equals(check)) {
                e.setDamage(e.getDamage() + check.getDamage());
                damage_record.remove(i);
            }
        }*/
        damage_record.add(e);
    }

    public static void deathReport(Player player, boolean remove) {
        int count = 1;
        List<SmashDamageEvent> to_remove = new ArrayList<SmashDamageEvent>();
        for (int i = damage_record.size() - 1; i >= 0; i--) {
            SmashDamageEvent e = damage_record.get(i);
            // Expunge old records
            if ((System.currentTimeMillis() - e.getDamageTimeMs()) > 15000) {
                to_remove.add(e);
                continue;
            }
            if (!e.getDamageeName().equals(player.getName())) {
                continue;
            }
            String time_since = String.format("%.1f", (System.currentTimeMillis() - e.getDamageTimeMs()) / 1000.0);
            String damage_amount = "Infinite";
            if (e.getDamage() < 1000) {
                damage_amount = String.format("%.1f", e.getDamage());
            }
            player.sendMessage(ChatColor.DARK_GREEN + "#" + count + ": " +
                    ChatColor.YELLOW + e.getDamagerName() + " " +
                    ChatColor.GRAY + "[" + ChatColor.YELLOW + damage_amount + ChatColor.GRAY + "] [" +
                    ChatColor.GREEN + e.getReason() + ChatColor.GRAY + "] [" +
                    ChatColor.GREEN + time_since + " Seconds Prior" + ChatColor.GRAY + "]");
            count++;
            to_remove.add(e);
        }
        if(remove) {
            damage_record.removeAll(to_remove);
        }
    }

    public static SmashDamageEvent getLastDamageEvent(Player player) {
        long last_time = 0;
        SmashDamageEvent record = null;
        // Check for ones with a damaging entity first
        for (int i = 0; i < damage_record.size(); i++) {
            SmashDamageEvent check = damage_record.get(i);
            if(check.getDamager() == null) {
                continue;
            }
            if(!(check.getDamager() instanceof Player)) {
                continue;
            }
            if (!check.getDamageeName().equals(player.getName())) {
                continue;
            }
            if(!check.getDisplayAsLastDamage()) {
                continue;
            }
            // Ignore old records
            if ((System.currentTimeMillis() - check.getDamageTimeMs()) > 15000) {
                continue;
            }
            if (check.getDamageTimeMs() <= last_time) {
                continue;
            }
            last_time = check.getDamageTimeMs();
            record = check;
        }
        if(record != null) {
            return record;
        }
        // Now check all records if we didn't find one
        for (int i = 0; i < damage_record.size(); i++) {
            SmashDamageEvent check = damage_record.get(i);
            if (!check.getDamageeName().equals(player.getName())) {
                continue;
            }
            if (check.getDamageTimeMs() <= last_time) {
                continue;
            }
            // Ignore old records
            if ((System.currentTimeMillis() - check.getDamageTimeMs()) > 15000) {
                continue;
            }
            last_time = check.getDamageTimeMs();
            record = check;
        }
        return record;
    }

}
