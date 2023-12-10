package ssm.utilities;

import java.util.Collection;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutRespawn;
import net.minecraft.server.v1_8_R3.WorldSettings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import ssm.Main;

public class SkinsUtil {

    private Player player;
    private Collection<PotionEffect> effects;
    private Location location;
    private int slot;
    private Property original_property = null;

    public SkinsUtil(Player player) {
        this.player = player;
    }

    public void changeSkin(String data, String signature) {
        EntityPlayer ePlayer = ((CraftPlayer) player).getHandle();
        GameProfile profile = ePlayer.getProfile();
        PropertyMap pMap = profile.getProperties();
        Property property = pMap.get("textures").iterator().next();
        if(original_property == null) {
            original_property = new Property(property.getName(), property.getValue(), property.getSignature());
        }
        pMap.remove("textures", property);
        pMap.put("textures", new Property("textures", data, signature));
        updateSkin();
    }

    public void removeSkin() {
        if(original_property != null) {
            changeSkin(original_property.getValue(), original_property.getSignature());
        }
    }

    public void updateSkin() {
        effects = player.getActivePotionEffects();
        location = player.getLocation();
        slot = player.getInventory().getHeldItemSlot();

        CraftWorld world = (CraftWorld) location.getWorld();
        CraftPlayer craftPlayer = ((CraftPlayer) player);
        EntityPlayer entityPlayer = craftPlayer.getHandle();
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, craftPlayer.getHandle()));
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, craftPlayer.getHandle()));
        //entityPlayer.playerConnection.sendPacket(new PacketPlayOutRespawn(world.getHandle().getDimensionManager(), world.getHandle().getDimensionKey(), world.getEnvironment().getId(), getGamemode(), getGamemode(), false, false, false));

        player.teleport(location);
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.hidePlayer(this.player);
            player.showPlayer(this.player);
        }

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {public void run() {
            player.getInventory().setHeldItemSlot(slot);
            player.addPotionEffects(effects);
            player.setExp(player.getExp());
            player.setHealth(player.getHealth()-0.0001);
            player.openInventory(player.getEnderChest());
            player.closeInventory();
        }}, 2);
    }

    public WorldSettings.EnumGamemode getGamemode() {
        switch (player.getGameMode()) {
            case SURVIVAL:
                return WorldSettings.EnumGamemode.SURVIVAL;
            case CREATIVE:
                return WorldSettings.EnumGamemode.CREATIVE;
            case SPECTATOR:
                return WorldSettings.EnumGamemode.SPECTATOR;
            default:
                return WorldSettings.EnumGamemode.ADVENTURE;
        }
    }

}
