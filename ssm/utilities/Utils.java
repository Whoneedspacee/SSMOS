package ssm.utilities;

import ssm.attributes.Attribute;
import ssm.managers.DamageManager;
import ssm.managers.DisguiseManager;
import ssm.Main;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.*;

public class Utils {

    /**
     * Sends a message to the player which appears over their hotbar (e.g, Cooldown Info, Music Disc Info...)
     *
     * @param message message being sent to the player
     * @param player  player receiving the message
     */
    public static void sendActionBarMessage(String message, Player player) {
        PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(message), (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public static void sendServerMessageToPlayer(String message, Player player, ServerMessageType type) {
        player.sendMessage(type + " " + message);
    }

    public static void sendAttributeMessage(Attribute attribute, Player player, ServerMessageType type) {
        sendAttributeMessage(attribute.getUseMessage(), attribute.name, player, type);
    }

    public static void sendAttributeMessage(String primary_message, String secondary_message, Player player, ServerMessageType type) {
        sendServerMessageToPlayer("§7" + primary_message + " §a" + secondary_message + "§7.", player, type);
    }

    public static void sendTitleMessage(Player player, String title_string, String subtitle_string) {
        sendTitleMessage(player, title_string, subtitle_string, 20, 60, 20);
    }

    public static void sendTitleMessage(Player player, String title_string, String subtitle_string,
                                        int fade_in_ticks, int stay_ticks, int fade_out_ticks) {
        // Prevent crashing
        if (title_string == null) {
            title_string = "";
        }
        if (subtitle_string == null) {
            subtitle_string = "";
        }
        PacketPlayOutTitle timing_packet = new PacketPlayOutTitle(fade_in_ticks, stay_ticks, fade_out_ticks);
        Utils.sendPacket(player, timing_packet);
        ChatMessage subtitle_message = new ChatMessage(subtitle_string);
        PacketPlayOutTitle subtitle_packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitle_message);
        Utils.sendPacket(player, subtitle_packet);
        ChatMessage title_message = new ChatMessage(title_string);
        PacketPlayOutTitle title_packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, title_message);
        Utils.sendPacket(player, title_packet);
    }

    public static String progressString(float exp) {
        String out = "";
        for (int i = 0; i < 40; i++) {
            float cur = i * (1f / 40f);
            if (cur < exp) {
                out += ChatColor.GREEN + "" + ChatColor.BOLD + "|";
            } else {
                out += ChatColor.GRAY + "" + ChatColor.BOLD + "|";
            }
        }
        return out;
    }

    public static boolean holdingItemWithName(Player player, String name) {
        if (player == null || player.getInventory().getItemInHand() == null) {
            return false;
        }
        ItemMeta itemMeta = player.getInventory().getItemInHand().getItemMeta();
        if (itemMeta != null) {
            String itemname = itemMeta.getDisplayName();
            if (itemname != null) {
                return itemname.equals(name);
            }
        }

        return false;
    }

    /**
     * Convert milliseconds to seconds with one tenth degree of accuracy
     *
     * @return seconds
     */
    public static double msToSeconds(long milliseconds) {
        return (long) (milliseconds / (double) 100) / (double) 10;
    }

    public static boolean entityIsOnGround(Entity ent) {
        return entityIsOnGround(ent, 0.5);
    }

    public static boolean entityIsOnGround(Entity ent, double distance) {
        if (ent == null) {
            return false;
        }
        World world = ent.getWorld();
        Location location = ent.getLocation();
        // Hitbox Edges
        double[] coords = {-0.3, 0, 0.3};
        for (double x : coords) {
            for (double z : coords) {
                if (!world.getBlockAt(ent.getLocation().subtract(x, distance, z)).getType().isTransparent()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean checkLeapEndGrounded(Entity ent) {
        return ent.isOnGround();
    }

    public static HashMap<LivingEntity, Double> getInRadius(Location location, double radius) {
        HashMap<LivingEntity, Double> ents = new HashMap<>();
        for (Entity cur : location.getWorld().getEntities()) {
            if (!(cur instanceof LivingEntity)) {
                continue;
            }
            LivingEntity ent = (LivingEntity) cur;
            if (!DamageUtil.canDamage(ent, null)) {
                continue;
            }
            //Feet
            double offset = location.distance(ent.getLocation());
            if (offset < radius) {
                ents.put(ent, 1 - (offset / radius));
                continue;
            }
            //Eyes
            offset = location.distance(ent.getEyeLocation());
            if (offset < radius) {
                ents.put(ent, 1 - (offset / radius));
            }
        }
        return ents;
    }

    public static org.bukkit.block.Block getTargetBlock(Player player, int distance) {
        org.bukkit.block.Block target = null;
        Iterator<Block> itr = new BlockIterator(player, distance);
        while (itr.hasNext()) {
            org.bukkit.block.Block block = itr.next();
            if (!block.getType().isSolid() || block.isLiquid()) {
                continue;
            }
            target = block;
            break;
        }
        return target;
    }

    public static void playFirework(Location loc, FireworkEffect.Type type, Color color, boolean flicker, boolean trail) {
        playFirework(loc, FireworkEffect.builder().flicker(flicker).withColor(color).with(type).trail(trail).build());
    }

    public static void playFirework(Location loc, FireworkEffect fe) {
        Firework firework = loc.getWorld().spawn(loc, Firework.class);

        FireworkMeta data = firework.getFireworkMeta();
        data.clearEffects();
        data.setPower(1);
        data.addEffect(fe);
        firework.setFireworkMeta(data);

        ((CraftFirework) firework).getHandle().expectedLifespan = 1;
    }

    public static void fullHeal(LivingEntity livingEntity) {
        livingEntity.setHealth(livingEntity.getMaxHealth());
        livingEntity.setFireTicks(0);
        if (livingEntity instanceof Player) {
            Player player = (Player) livingEntity;
            player.setFoodLevel(20);
            player.setSaturation(3);
            player.setLevel(0);
            player.setExp(0);
        }
    }

    public static double getXZDistance(Location first, Location second) {
        return Math.sqrt(Math.pow(first.getX() - second.getX(), 2) + Math.pow(first.getZ() - second.getZ(), 2));
    }

    public static void playParticle(EnumParticle particle, Location location, float offsetX, float offsetY, float offsetZ,
                                    float speed, int count, int dist, Collection<Player> players) {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(particle, true,
                (float) location.getX(), (float) location.getY(), (float) location.getZ(),
                offsetX, offsetY, offsetZ, speed, count, dist);

        for (Player player : players) {
            if (player.getLocation().distance(location) > dist)
                continue;

            Utils.sendPacket(player, packet);
        }
    }

    public static void sendPacket(Player player, Packet packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public static void sendPacketToAllBut(Player exclude, Packet packet) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.equals(exclude)) {
                continue;
            }
            sendPacket(player, packet);
        }
    }

    public static void sendPacketToAll(Packet packet) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendPacket(player, packet);
        }
    }

    public static void creatureMove(Entity ent, Location target, float speed) {
        if (!(ent instanceof Creature))
            return;

        if (ent.getLocation().toVector().distanceSquared(target.toVector()) < 0.01)
            return;

        EntityCreature ec = ((CraftCreature) ent).getHandle();
        NavigationAbstract nav = ec.getNavigation();

        if (ent.getLocation().toVector().distanceSquared(target.toVector()) > 16 * 16) {
            Location newTarget = ent.getLocation();

            newTarget.add(target.toVector().clone().subtract(ent.getLocation().toVector().clone()).normalize().multiply(16));

            nav.a(newTarget.getX(), newTarget.getY(), newTarget.getZ(), speed);
        } else {
            nav.a(target.getX(), target.getY(), target.getZ(), speed);
        }
    }

    public static <T extends Entity> List<T> getEntitiesInsideEntity(Entity ent, List<T> entities) {
        AxisAlignedBB box = ((CraftEntity) ent).getHandle().getBoundingBox();

        List<T> list = new ArrayList<>();

        for (T e : entities) {
            AxisAlignedBB box2 = ((CraftEntity) e).getHandle().getBoundingBox();
            if (box2.b(box)) list.add(e);
        }
        return list;
    }

    public static boolean isInsideBoundingBox(Entity ent, Vector a, Vector b) {
        AxisAlignedBB box = ((CraftEntity) ent).getHandle().getBoundingBox();
        AxisAlignedBB box2 = new AxisAlignedBB(a.getX(), a.getY(), a.getZ(), b.getX(), b.getY(), b.getZ());
        return box.b(box2);
    }

    public static boolean hitBox(Location loc, LivingEntity ent, double mult, EntityType disguise) {
        if (disguise != null) {
            if (disguise == EntityType.SQUID) {
                return loc.distance(ent.getLocation().add(0, 0.4, 0)) < 0.6 * mult;
            }
        }

        Location loc2d = loc.clone();
        loc2d.setY(0);
        Location entloc2d = ent.getLocation().clone();
        entloc2d.setY(0);

        if (ent instanceof Player) {
            Player player = (Player) ent;

            if (loc.distance(player.getEyeLocation()) < 0.4 * mult) {
                return true;
            } else if (loc2d.distance(entloc2d) < 0.6 * mult) {
                if (loc.getY() >= player.getLocation().getY() - 0.2 * mult && loc.getY() <= player.getEyeLocation().getY() + 0.2 * mult) {
                    return true;
                }
            }
        } else {
            if (ent instanceof Giant) {
                if (loc.getY() > ent.getLocation().getY() && loc.getY() < ent.getLocation().getY() + 12)
                    if (loc2d.distance(entloc2d) < 4)
                        return true;
            } else {
                if (loc.getY() > ent.getLocation().getY() && loc.getY() < ent.getLocation().getY() + 2)
                    if (loc2d.distance(entloc2d) < 0.5 * mult)
                        return true;
            }
        }
        return false;
    }

    public static List<Player> getNearby(Location location, double maxDist) {
        LinkedList<Player> nearbyMap = new LinkedList<>();
        for (Player cur : location.getWorld().getPlayers()) {
            if (!DamageUtil.canDamage(cur, null)) {
                continue;
            }
            if (cur.isDead()) {
                continue;
            }
            double dist = location.distance(cur.getLocation());
            if (dist > maxDist) {
                continue;
            }
            for (int i = 0; i < nearbyMap.size(); i++) {
                if (dist < location.distance(nearbyMap.get(i).getLocation())) {
                    nearbyMap.add(i, cur);
                    break;
                }
            }
            if (!nearbyMap.contains(cur)) {
                nearbyMap.addLast(cur);
            }
        }
        return nearbyMap;
    }

    public static void itemEffect(Location location, int item_count, double velocity,
                                  Sound sound, float sound_volume, float sound_pitch, Material type, byte data, long ticks) {
        if (type != null && type != Material.AIR) {
            List<Entity> items = new ArrayList<>();
            ItemStack itemStack = new ItemStack(type);
            itemStack.setDurability(data);
            for (int i = 0; i < item_count; i++) {
                Item item = location.getWorld().dropItem(location, itemStack);
                item.setVelocity(new Vector((Math.random() - 0.5) * velocity, Math.random() * velocity, (Math.random() - 0.5) * velocity));
                item.setPickupDelay(999999);
                items.add(item);
            }
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
                @Override
                public void run() {
                    for (Entity ent : items) {
                        ent.remove();
                    }
                }
            }, ticks);
        }
        if (sound != null) {
            location.getWorld().playSound(location, sound, sound_volume, sound_pitch);
        }
    }

    // This will make it so you cannot teleport entities since they have passengers
    public static void attachCustomName(Entity entity, String name) {
        if(entity == null) {
            return;
        }
        net.minecraft.server.v1_8_R3.Entity nms_entity = ((CraftEntity) entity).getHandle();
        Squid squid = (Squid) entity.getWorld().spawnEntity(new Location(entity.getWorld(),
                nms_entity.locX, nms_entity.locY + nms_entity.an(), nms_entity.locZ), EntityType.SQUID);
        EntitySquid nms_squid = ((CraftSquid) squid).getHandle();
        ArmorStand armor_stand = (ArmorStand) entity.getWorld().spawnEntity(new Location(entity.getWorld(),
                nms_entity.locX, nms_squid.locY + nms_squid.an(), nms_entity.locZ), EntityType.ARMOR_STAND);
        armor_stand.setCustomName(name);
        armor_stand.setCustomNameVisible(true);
        armor_stand.setVisible(false);
        armor_stand.setMarker(true);
        // Invisibility for Squid
        squid.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
        // Invincibility
        DamageManager.invincible_mobs.put(squid, 1);
        // Apply melees from squid to entity
        DisguiseManager.redirect_melee.put(squid, entity);
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if(!entity.isValid()) {
                    DisguiseManager.redirect_melee.remove(squid);
                    DamageManager.invincible_mobs.remove(squid);
                    armor_stand.remove();
                    squid.remove();
                    cancel();
                    return;
                }
                squid.setPassenger(armor_stand);
                entity.setPassenger(squid);
            }
        };
        runnable.runTaskTimer(Main.getInstance(), 0L, 0L);
    }

    public static String getAttachedCustomName(Entity entity) {
        if(entity.getPassenger() == null) {
            return null;
        }
        if(!(entity.getPassenger().getPassenger() instanceof ArmorStand)) {
            return null;
        }
        ArmorStand armor_stand = (ArmorStand) entity.getPassenger().getPassenger();
        return armor_stand.getCustomName();
    }

}
