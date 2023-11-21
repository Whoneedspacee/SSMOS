package ssm.managers.maps;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.material.Wool;
import org.bukkit.util.Vector;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class GameMap extends SmashMap {

    protected String created_by = "N/A";
    private List<Location> respawn_points = new ArrayList<Location>();
    protected List<Player> voted = new ArrayList<Player>();
    private Vector boundary_min = null;
    private Vector boundary_max = null;

    public GameMap(File file) {
        super(file);
        this.parse_chunk_radius = 10;
        name = file.getName().replace("_", " ");
        name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        File name_file = new File(map_directory.getPath() + "/map_name.txt");
        File created_by_file = new File(map_directory.getPath() + "/created_by.txt");
        try {
            if (name_file.exists() && name_file.length() != 0) {
                name = Files.readString(name_file.toPath());
            }
            else {
                if(!name_file.exists()) {
                    name_file.createNewFile();
                }
            }
            if(created_by_file.exists() && created_by_file.length() != 0) {
                created_by = Files.readString(created_by_file.toPath());
            }
            else {
                if(!created_by_file.exists()) {
                    created_by_file.createNewFile();
                }
            }
        } catch (Exception e) {
            Bukkit.broadcastMessage(ChatColor.RED + "Failed to read map files");
        }
    }

    @Override
    public void createWorld() {
        respawn_points.clear();
        boundary_min = null;
        boundary_max = null;
        super.createWorld();
    }

    public boolean parseBlock(Block parsed) {
        if(isRespawnPoint(parsed)) {
            respawn_points.add(parsed.getLocation());
            return true;
        }
        if(isBoundaryPoint(parsed)) {
            addBoundaryPoint(parsed.getLocation());
            return true;
        }
        if(isCenterPoint(parsed)) {
            world.setSpawnLocation(parsed.getX(), parsed.getY(), parsed.getZ());
            return true;
        }
        return false;
    }

    public List<Location> getRespawnPoints() {
        return respawn_points;
    }

    public String getCreatedBy() {
        return created_by;
    }

    public List<Player> getVoted() {
        for (Player player : voted) {
            if (!player.isOnline()) {
                voted.remove(player);
            }
        }
        return voted;
    }

    public void clearVoted() {
        voted.clear();
    }

    public void addBoundaryPoint(Location location) {
        if(boundary_min == null) {
            boundary_min = location.toVector();
        }
        if(boundary_max == null) {
            boundary_max = location.toVector();
        }
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        boundary_min.setX(Math.min(x, boundary_min.getX()));
        boundary_min.setY(Math.min(y, boundary_min.getY()));
        boundary_min.setZ(Math.min(z, boundary_min.getZ()));
        boundary_max.setX(Math.max(x, boundary_max.getX()));
        boundary_max.setY(Math.max(y, boundary_max.getY()));
        boundary_max.setZ(Math.max(z, boundary_max.getZ()));
    }

    public boolean isOutOfBounds(Entity entity) {
        if(boundary_min == null || boundary_max == null) {
            return false;
        }
        if(entity == null) {
            return false;
        }
        if(world == null || !entity.getWorld().equals(world)) {
            return false;
        }
        Vector vector = entity.getLocation().toVector();
        if(vector.getX() < boundary_min.getX() || vector.getX() > boundary_max.getX()) {
            return true;
        }
        if(vector.getY() < boundary_min.getY() || vector.getY() > boundary_max.getY()) {
            return true;
        }
        if(vector.getZ() < boundary_min.getZ() || vector.getZ() > boundary_max.getZ()) {
            return true;
        }
        return false;
    }

    public boolean isRespawnPoint(Block check) {
        if (check.getType() != Material.WOOL) {
            return false;
        }
        Wool wool = (Wool) check.getState().getData();
        if (wool.getColor() != DyeColor.GREEN) {
            return false;
        }
        Block plate = check.getRelative(0, 1, 0);
        return (plate.getType() == Material.GOLD_PLATE);
    }

    public boolean isBoundaryPoint(Block check) {
        if (check.getType() != Material.WOOL) {
            return false;
        }
        Wool wool = (Wool) check.getState().getData();
        if (wool.getColor() != DyeColor.RED) {
            return false;
        }
        Block plate = check.getRelative(0, 1, 0);
        return (plate.getType() == Material.GOLD_PLATE);
    }

    public boolean isCenterPoint(Block check) {
        if (check.getType() != Material.WOOL) {
            return false;
        }
        Wool wool = (Wool) check.getState().getData();
        if (wool.getColor() != DyeColor.WHITE) {
            return false;
        }
        Block plate = check.getRelative(0, 1, 0);
        return (plate.getType() == Material.GOLD_PLATE);
    }

    public String toString() {
        return ChatColor.GREEN + "Map - " + ChatColor.RESET + ChatColor.BOLD + getName() +
                ChatColor.GRAY + " created by " + ChatColor.RESET + ChatColor.BOLD + getCreatedBy();
    }
}
