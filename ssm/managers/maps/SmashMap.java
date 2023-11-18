package ssm.managers.maps;

import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import ssm.commands.CommandLoadWorld;

import java.io.File;
import java.util.List;
import java.util.UUID;

public abstract class SmashMap {

    protected String name = "N/A";
    protected File map_directory;
    protected World world = null;
    protected int parse_radius = 0;

    public SmashMap(File original_directory) {
        File copy_directory = new File("maps/_Copies/" + UUID.randomUUID());
        if (!copy_directory.exists()) {
            try {
                FileUtils.copyDirectory(original_directory, copy_directory);
                File uid = new File(copy_directory.getPath() + "/uid.dat");
                File session = new File(copy_directory.getPath() + "/session.dat");
                if (uid.exists()) {
                    uid.delete();
                }
                if (session.exists()) {
                    session.delete();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        map_directory = copy_directory;
    }

    public void createWorld() {
        World existing_world = Bukkit.getWorld(map_directory.getPath());
        if (existing_world != null) {
            return;
        }
        world = CommandLoadWorld.loadWorld(map_directory.getPath());
        Block center = world.getSpawnLocation().getBlock();
        // Parse map for other objects
        for (int x = -parse_radius; x <= parse_radius; x++) {
            for (int y = -parse_radius; y <= parse_radius; y++) {
                for (int z = -parse_radius; z <= parse_radius; z++) {
                    Block parsed = center.getRelative(x, y, z);
                    if (parseBlock(parsed)) {
                        parsed.getRelative(0, 1, 0).setType(Material.AIR);
                        parsed.setType(Material.AIR);
                    }
                }
            }
        }
    }

    public void deleteWorld() {
        unloadWorld();
        try {
            FileUtils.deleteDirectory(map_directory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unloadWorld() {
        if(world == null) {
            return;
        }
        for (Player player : world.getPlayers()) {
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        }
        Bukkit.unloadWorld(world, false);
    }

    public String getName() {
        return name;
    }

    public File getMapDirectory() {
        return map_directory;
    }

    public World getWorld() {
        return world;
    }

    public List<Location> getRespawnPoints() {
        if (world == null) {
            return null;
        }
        return List.of(world.getSpawnLocation());
    }

    public boolean isOutOfBounds(Entity entity) {
        return (entity.getLocation().getY() <= 0);
    }

    public boolean parseBlock(Block parsed) {
        return false;
    }

}
