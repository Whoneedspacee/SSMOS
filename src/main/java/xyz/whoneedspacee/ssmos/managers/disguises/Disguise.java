package xyz.whoneedspacee.ssmos.managers.disguises;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import xyz.whoneedspacee.ssmos.managers.gamestate.GameState;
import xyz.whoneedspacee.ssmos.commands.CommandShowHealth;
import xyz.whoneedspacee.ssmos.managers.GameManager;
import xyz.whoneedspacee.ssmos.managers.smashscoreboard.SmashScoreboard;
import xyz.whoneedspacee.ssmos.managers.smashserver.SmashServer;
import xyz.whoneedspacee.ssmos.utilities.Utils;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Disguise {

    protected String name;
    protected EntityType type;
    protected Player owner;
    protected EntityLiving living;
    protected EntityArmorStand armorstand;
    protected EntitySquid squid;
    protected boolean showAttackAnimation = true;
    protected org.bukkit.entity.Entity target = null;
    protected List<Player> viewers = new ArrayList<Player>();
    protected HashMap<Player, Double> last_viewer_distance = new HashMap<Player, Double>();

    public Disguise(Player owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public EntityType getType() {
        return type;
    }

    public Player getOwner() {
        return owner;
    }

    public boolean isViewer(Player player) {
        return viewers.contains(player);
    }

    public EntityLiving getLiving() {
        return living;
    }

    public EntityArmorStand getArmorStand() {
        return armorstand;
    }

    public EntitySquid getSquid() {
        return squid;
    }

    public void spawnLiving() {
        if (living != null) {
            deleteLiving();
        }
        living = newLiving();
        armorstand = new EntityArmorStand(((CraftWorld) owner.getWorld()).getHandle());
        // Had no idea this existed, but seems like this is what other servers must be doing
        ((CraftArmorStand) armorstand.getBukkitEntity()).setMarker(true);
        squid = new EntitySquid(((CraftWorld) owner.getWorld()).getHandle());
        for (Player player : owner.getWorld().getPlayers()) {
            if (player.equals(owner)) {
                continue;
            }
            showDisguise(player);
        }
    }

    public void reloadDisguise(Player player) {
        hideDisguise(player);
        showDisguise(player);
    }

    public void reloadLiving(Player player) {
        // Living Destroy (if player sees them already)
        PacketPlayOutEntityDestroy destroy_packet = new PacketPlayOutEntityDestroy(living.getId());
        Utils.sendPacket(player, destroy_packet);
        // Set exact head rotation before entity is spawned since setPositionRotation does not do that
        // The body of a mob is rotated on the client so this will make the body spawn already rotated
        living.f(owner.getLocation().getYaw());
        // Spawn living
        PacketPlayOutSpawnEntityLiving living_packet = new PacketPlayOutSpawnEntityLiving(living);
        Utils.sendPacket(player, living_packet);
    }

    public void showDisguise(Player player) {
        // Do not show disguise to self
        if (player.equals(owner)) {
            return;
        }
        // Can't show disguise if it doesn't exist
        if(living == null) {
            return;
        }
        viewers.add(player);
        // Armor Stand Destroy (if player sees them already)
        PacketPlayOutEntityDestroy destroy_packet = new PacketPlayOutEntityDestroy(armorstand.getId());
        Utils.sendPacket(player, destroy_packet);
        // Squid Destroy (if player sees them already)
        destroy_packet = new PacketPlayOutEntityDestroy(squid.getId());
        Utils.sendPacket(player, destroy_packet);
        // Living Destroy (if player sees them already)
        destroy_packet = new PacketPlayOutEntityDestroy(living.getId());
        Utils.sendPacket(player, destroy_packet);
        // Living Spawn
        living.setPositionRotation(owner.getLocation().getX(), owner.getLocation().getY(), owner.getLocation().getZ(),
                    owner.getLocation().getYaw(), owner.getLocation().getPitch());
        // Set exact head rotation before entity is spawned since setPositionRotation does not do that
        // The body of a mob is rotated on the client so this will make the body spawn already rotated
        living.f(owner.getLocation().getYaw());
        PacketPlayOutSpawnEntityLiving living_packet = new PacketPlayOutSpawnEntityLiving(living);
        Utils.sendPacket(player, living_packet);
        // Squid Spawn
        squid.setPositionRotation(owner.getLocation().getX(), living.locY + living.an() + squid.am(), owner.getLocation().getZ(),
                owner.getLocation().getYaw(), owner.getLocation().getPitch());
        PacketPlayOutSpawnEntityLiving squid_packet = new PacketPlayOutSpawnEntityLiving(squid);
        Utils.sendPacket(player, squid_packet);
        // Armor Stand Spawn
        armorstand.setPositionRotation(owner.getLocation().getX(), squid.locY + squid.an() + armorstand.am(), owner.getLocation().getZ(),
                owner.getLocation().getYaw(), owner.getLocation().getPitch());
        PacketPlayOutSpawnEntityLiving armorstand_packet = new PacketPlayOutSpawnEntityLiving(armorstand);
        Utils.sendPacket(player, armorstand_packet);
        // Invisibility for Armor Stand
        DataWatcher dw = armorstand.getDataWatcher();
        dw.watch(0, (byte) 0x20);
        PacketPlayOutEntityMetadata invisiblity_packet = new PacketPlayOutEntityMetadata(armorstand.getId(), dw, true);
        Utils.sendPacket(player, invisiblity_packet);
        // Invisibility for Squid
        dw = squid.getDataWatcher();
        dw.watch(0, (byte) 0x20);
        invisiblity_packet = new PacketPlayOutEntityMetadata(squid.getId(), dw, true);
        Utils.sendPacket(player, invisiblity_packet);
        update();
    }

    public void hideDisguise(Player player) {
        PacketPlayOutEntityDestroy destroy_living_packet = new PacketPlayOutEntityDestroy(living.getId());
        PacketPlayOutEntityDestroy destroy_armorstand_packet = new PacketPlayOutEntityDestroy(armorstand.getId());
        PacketPlayOutEntityDestroy destroy_squid_packet = new PacketPlayOutEntityDestroy(squid.getId());
        Utils.sendPacket(player, destroy_living_packet);
        Utils.sendPacket(player, destroy_armorstand_packet);
        Utils.sendPacket(player, destroy_squid_packet);
        showOwner();
        viewers.remove(player);
    }

    public void update() {
        if(living == null) {
            return;
        }
        Location location = owner.getLocation();
        // Hide the disguised player from other players
        // Don't use HidePlayer here, it stops the melees
        // But also stops the server from recognizing projectile hits like arrows
        hideOwner();
        // Hide the mob from the disguised player
        PacketPlayOutEntityDestroy disguise_destroy_packet = new PacketPlayOutEntityDestroy(living.getId());
        Utils.sendPacket(owner, disguise_destroy_packet);
        // Don't teleport to spectator player if the mob is dead
        if (living.dead) {
            return;
        }
        for(Player player : viewers) {
            if(!player.getWorld().equals(living.getBukkitEntity().getWorld())) {
                continue;
            }
            Location player_xz = player.getLocation();
            player_xz.setY(0);
            Location living_xz = living.getBukkitEntity().getLocation();
            living_xz.setY(0);
            double distance = player_xz.distance(living_xz);
            double distance_check = 32;
            last_viewer_distance.putIfAbsent(player, distance);
            if(last_viewer_distance.get(player) > distance_check && distance <= distance_check) {
                reloadLiving(player);
            }
            last_viewer_distance.put(player, distance);
        }
        // Update nametag
        for(Player viewer : viewers) {
            String custom_name = "";
            SmashServer server = GameManager.getPlayerServer(viewer);
            if(CommandShowHealth.show_health || (server != null && server.getLives(viewer) <= 0)) {
                if(server != null && server.getState() >= GameState.GAME_STARTING) {
                    custom_name += ChatColor.RED + "❤ " + ChatColor.WHITE + String.format("%.2f", owner.getHealth()) + " ";
                }
            }
            custom_name += ChatColor.RESET + SmashScoreboard.getPlayerColor(owner, false) + owner.getName();
            DataWatcher watcher = new DataWatcher(null);
            watcher.a(2, custom_name);
            watcher.a(3, (byte) 1);
            PacketPlayOutEntityMetadata data_packet = new PacketPlayOutEntityMetadata(armorstand.getId(), watcher, true);
            Utils.sendPacket(viewer, data_packet);
        }
        living.onGround = Utils.entityIsOnGround(owner);
        if(target != null) {
            Vector direction = target.getLocation().toVector().subtract(location.toVector());
            location.setDirection(direction);
        }
        living.setPositionRotation(location.getX(), location.getY(), location.getZ(),
                location.getYaw(), location.getPitch());
        PacketPlayOutEntityTeleport teleport_packet = new PacketPlayOutEntityTeleport(living);
        Utils.sendPacketToAllBut(owner, teleport_packet);
        PacketPlayOutEntityHeadRotation head_packet = new PacketPlayOutEntityHeadRotation(living,
                (byte) ((location.getYaw() * 256.0F) / 360.0F));
        Utils.sendPacketToAllBut(owner, head_packet);
        // From living.mount source code all the way to Entity.class mount
        // In the Entity.class al() method appears to be where it sets the passengers position
        squid.setPositionRotation(location.getX(), living.locY + living.an() + squid.am(), location.getZ(),
                owner.getLocation().getYaw(), owner.getLocation().getPitch());
        teleport_packet = new PacketPlayOutEntityTeleport(squid);
        Utils.sendPacketToAllBut(owner, teleport_packet);
        armorstand.setPositionRotation(location.getX(), squid.locY + squid.an() + armorstand.am(), location.getZ(),
                owner.getLocation().getYaw(), owner.getLocation().getPitch());
        teleport_packet = new PacketPlayOutEntityTeleport(armorstand);
        Utils.sendPacketToAllBut(owner, teleport_packet);
        // Show player data
        DataWatcher dw = living.getDataWatcher();
        byte player_data = 0;
        if(owner.getFireTicks() > 0) {
            player_data = (byte) (player_data | 0x01);
        }
        if(owner.isSneaking()) {
            player_data = (byte) (player_data | 0x02);
        }
        if(owner.isSprinting()) {
            player_data = (byte) (player_data | 0x08);
        }
        dw.watch(0, player_data);
        PacketPlayOutEntityMetadata data_packet = new PacketPlayOutEntityMetadata(living.getId(), dw, true);
        Utils.sendPacketToAllBut(owner, data_packet);
        // Keep the entity loaded by sending status 0 packets repeatedly
        PacketPlayOutEntityStatus status_packet = new PacketPlayOutEntityStatus(living, (byte) 0);
        Utils.sendPacketToAllBut(owner, status_packet);
    }

    public void deleteLiving() {
        if (living == null) {
            return;
        }
        PacketPlayOutEntityDestroy destroy_living_packet = new PacketPlayOutEntityDestroy(living.getId());
        PacketPlayOutEntityDestroy destroy_armorstand_packet = new PacketPlayOutEntityDestroy(armorstand.getId());
        PacketPlayOutEntityDestroy destroy_squid_packet = new PacketPlayOutEntityDestroy(squid.getId());
        for (Player player : Bukkit.getOnlinePlayers()) {
            Utils.sendPacket(player, destroy_living_packet);
            Utils.sendPacket(player, destroy_armorstand_packet);
            Utils.sendPacket(player, destroy_squid_packet);
        }
        showOwner();
        living = null;
        armorstand = null;
        squid = null;
    }

    public void hideOwner() {
        PacketPlayOutEntityDestroy destroy_packet = new PacketPlayOutEntityDestroy(owner.getEntityId());
        for(Player player : viewers) {
            Utils.sendPacket(player, destroy_packet);
        }
    }

    public void showOwner() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            // Hide and then re-show so bukkit will recognize the player as having been hidden
            player.hidePlayer(owner);
            player.showPlayer(owner);
        }
    }

    protected abstract EntityLiving newLiving();

    public boolean getShowAttackAnimation() {
        return showAttackAnimation;
    }

    public Sound getDamageSound() {
        Sound sound;
        switch (type) {
            default:
                sound = Sound.HURT_FLESH;
                break;
            case BAT:
                sound = Sound.BAT_HURT;
                break;
            case BLAZE:
                sound = Sound.BLAZE_HIT;
                break;
            case CAVE_SPIDER:
            case SPIDER:
                sound = Sound.SPIDER_IDLE;
                break;
            case CHICKEN:
                sound = Sound.CHICKEN_HURT;
                break;
            case COW:
            case MUSHROOM_COW:
                sound = Sound.COW_HURT;
                break;
            case CREEPER:
                sound = Sound.CREEPER_HISS;
                break;
            case ENDER_DRAGON:
                sound = Sound.ENDERDRAGON_HIT;
                break;
            case ENDERMAN:
                sound = Sound.ENDERMAN_HIT;
                break;
            case GHAST:
                sound = Sound.GHAST_SCREAM;
                break;
            case GIANT:
            case ZOMBIE:
                sound = Sound.ZOMBIE_HURT;
                break;
            case HORSE: // lmao virgin bukkit entity type doesn't differentiate between horse variants
                sound = Sound.HORSE_SKELETON_HIT;
                break;
            case IRON_GOLEM:
                sound = Sound.IRONGOLEM_HIT;
                break;
            case MAGMA_CUBE:
                sound = Sound.MAGMACUBE_JUMP;
                break;
            case OCELOT:
                sound = Sound.CAT_HIT;
                break;
            case PIG:
                sound = Sound.PIG_IDLE;
                break;
            case PIG_ZOMBIE:
                sound = Sound.ZOMBIE_PIG_HURT;
                break;
            case SHEEP:
                sound = Sound.SHEEP_IDLE;
                break;
            case SILVERFISH:
                sound = Sound.SILVERFISH_HIT;
                break;
            case SKELETON:
                sound = Sound.SKELETON_HURT;
                break;
            case SLIME:
                sound = Sound.SLIME_ATTACK;
                break;
            case SNOWMAN:
                sound = Sound.STEP_SNOW;
                break;
            case VILLAGER:
            case WITCH:
                sound = Sound.VILLAGER_HIT;
                break;
            case WITHER:
                sound = Sound.WITHER_HURT;
                break;
            case WOLF:
                sound = Sound.WOLF_HURT;
                break;
        }
        return sound;
    }

    public float getVolume() {
        return 1.0f;
    }

    public float getPitch() {
        return ((float) ((Math.random() - Math.random()) * 0.2f + 1.0f));
    }

    public void playDamageSound() {
        owner.getWorld().playSound(owner.getLocation(), getDamageSound(), getVolume(), getPitch());
    }

    // Leashes the living mob to the specified entity
    public void setAsLeashHolder(LivingEntity livingEntity) {
        Entity nms_vehicle = ((CraftEntity) livingEntity).getHandle();
        PacketPlayOutAttachEntity attach_living_packet = new PacketPlayOutAttachEntity(1, nms_vehicle, living);
        Utils.sendPacketToAllBut(owner, attach_living_packet);
    }

    // Unleashes the living mob
    public void removeLeashHolder(LivingEntity livingEntity) {
        Entity nms_vehicle = ((CraftEntity) livingEntity).getHandle();
        PacketPlayOutAttachEntity detach_living_packet = new PacketPlayOutAttachEntity(0, nms_vehicle, living);
        Utils.sendPacketToAllBut(owner, detach_living_packet);
    }

}
