package ssm.commands;

import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.material.Wool;
import ssm.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CommandWorld implements CommandExecutor {

    // Hardcoded but better than what was, not like this will be edited much I assume
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {
        if (!commandSender.isOp()) {
            return true;
        }
        if (args.length < 1 || args[0].equalsIgnoreCase("Help")) {
            printHelp(commandSender);
            return true;
        }
        String sub_command = args[0];
        if (sub_command.equalsIgnoreCase("list")) {
            listWorlds(commandSender);
            return true;
        }
        if (sub_command.equalsIgnoreCase("create")) {
            createWorld(commandSender, args);
            return true;
        }
        if (sub_command.equalsIgnoreCase("edit")) {
            searchEditWorld(commandSender, args);
            return true;
        }
        if (sub_command.equalsIgnoreCase("load")) {
            searchLoadWorld(commandSender, args);
            return true;
        }
        if (sub_command.equalsIgnoreCase("unload")) {
            searchUnloadWorld(commandSender, args);
            return true;
        }
        if (sub_command.equalsIgnoreCase("tp")) {
            searchTeleportWorld(commandSender, args);
            return true;
        }
        if (sub_command.equalsIgnoreCase("convert")) {
            convertWorld(commandSender);
            return true;
        }
        printHelp(commandSender);
        return true;
    }

    public static void printHeaderMessage(CommandSender commandSender, String header_message) {
        commandSender.sendMessage(ChatColor.RESET + "----- " + ChatColor.DARK_AQUA + header_message + ChatColor.RESET + " -----");
    }

    public static void printCommandMessage(CommandSender commandSender, String command, String description) {
        commandSender.sendMessage(ChatColor.GRAY + command + ChatColor.RESET + " - " + description);
    }

    public static void printHelp(CommandSender commandSender) {
        printHeaderMessage(commandSender, "World Commands");
        printCommandMessage(commandSender, "/world help", "Displays available commands");
        printCommandMessage(commandSender, "/world list", "Shows currently loaded worlds");
        printCommandMessage(commandSender, "/world create", "Copies a void world directory into the base maps folder with the name given");
        printCommandMessage(commandSender, "/world edit", "Loads and teleports you to one of the maps in the gamemode maps folder");
        printCommandMessage(commandSender, "/world load", "Loads the world by relative path to server folder");
        printCommandMessage(commandSender, "/world unload", "Unloads the current world or the best matched world by name");
        printCommandMessage(commandSender, "/world tp", "Teleports to the best matched world by name, lists options if no name given");
        printCommandMessage(commandSender, "/world convert", "Converts an old parse world into a new parse world by adding item frames to the points");
    }

    public static void listWorlds(CommandSender commandSender) {
        printHeaderMessage(commandSender, "World List");
        for (World world : Bukkit.getWorlds()) {
            commandSender.sendMessage(world.getName());
        }
    }

    public static void createWorld(CommandSender commandSender, String[] args) {
        if(args.length < 2) {
            commandSender.sendMessage("Please specify a world name.");
            return;
        }
        File maps_folder = new File("maps");
        if (!maps_folder.exists() || !maps_folder.isDirectory()) {
            commandSender.sendMessage("Could not find maps folder.");
            return;
        }
        File void_map_directory = new File("maps/_VoidWorld");
        if (!void_map_directory.exists() || !void_map_directory.isDirectory()) {
            commandSender.sendMessage("Could not find void map.");
            return;
        }
        StringBuilder name_builder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            name_builder.append(args[i]);
            name_builder.append(" ");
        }
        String name_string = name_builder.substring(0, name_builder.length() - 1);
        File create_world_directory = new File(maps_folder.getPath() + "/" + name_string);
        if(create_world_directory.exists()) {
            commandSender.sendMessage("World folder already exists.");
            return;
        }
        try {
            FileUtils.copyDirectory(void_map_directory, create_world_directory);
        } catch(Exception e) {
            e.printStackTrace();
        }
        if(create_world_directory.exists()) {
            commandSender.sendMessage("Successfully copied world folder.");
        } else {
            commandSender.sendMessage("Unable to create world folder.");
        }

    }

    public static void searchEditWorld(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof Player)) {
            return;
        }
        Player player = (Player) commandSender;
        File maps_folder = new File("maps");
        if (!maps_folder.exists() || !maps_folder.isDirectory()) {
            commandSender.sendMessage("Could not find maps folder.");
            return;
        }
        File[] files = maps_folder.listFiles();
        List<File> world_directories = new ArrayList<File>();
        if (files == null) {
            commandSender.sendMessage("Maps folder was empty.");
            return;
        }
        for (File file : files) {
            if (!file.isDirectory()) {
                continue;
            }
            if(file.getName().equalsIgnoreCase("_Copies")) {
                continue;
            }
            File region_directory = new File(file.getPath() + "/region");
            if (region_directory.exists()) {
                world_directories.add(file);
                continue;
            }
            File[] sub_files = file.listFiles();
            if (sub_files == null) {
                continue;
            }
            for (File sub_file : sub_files) {
                if (!sub_file.isDirectory()) {
                    continue;
                }
                File sub_region_directory = new File(sub_file.getPath() + "/region");
                if (sub_region_directory.exists()) {
                    world_directories.add(sub_file);
                }
            }
        }
        if (args.length < 2) {
            printHeaderMessage(commandSender, "World Options");
            for (File file : world_directories) {
                commandSender.sendMessage(file.getPath());
            }
            return;
        }
        StringBuilder path_builder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            path_builder.append(args[i]);
            path_builder.append(" ");
        }
        String path_string = path_builder.substring(0, path_builder.length() - 1);
        // Check for exact names first
        for (File file : world_directories) {
            if (file.getPath().equalsIgnoreCase(path_string)) {
                editWorld(player, file.getPath());
                return;
            }
        }
        // Check for matched names
        for (File file : world_directories) {
            if (file.getPath().contains(path_string)) {
                editWorld(player, file.getPath());
                return;
            }
        }
        commandSender.sendMessage("Could not find world.");
    }

    public static void editWorld(Player player, String world_directory_path) {
        World world = loadWorld(world_directory_path);
        if (world == null) {
            player.sendMessage("Failed to load world.");
            return;
        }
        player.sendMessage("Started Editing: " + world_directory_path);
        player.teleport(world.getSpawnLocation());
    }

    public static void searchLoadWorld(CommandSender commandSender, String[] args) {
        if (args.length < 2) {
            commandSender.sendMessage("Must specify a world to load.");
            return;
        }
        StringBuilder path_builder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            path_builder.append(args[i]);
            path_builder.append(" ");
        }
        String path_string = path_builder.substring(0, path_builder.length() - 1);
        World world = loadWorld(path_string);
        if (world == null) {
            commandSender.sendMessage("Failed to load World.");
            return;
        }
        commandSender.sendMessage("World Loaded!");
    }

    public static World loadWorld(String world_directory_path) {
        WorldCreator worldCreator = new WorldCreator(world_directory_path);
        return worldCreator.createWorld();
    }

    public static void searchUnloadWorld(CommandSender commandSender, String[] args) {
        if (args.length < 2) {
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                if (unloadWorld(player.getWorld().getName())) {
                    commandSender.sendMessage("World Saved!");
                } else {
                    commandSender.sendMessage("Could not unload world.");
                }
                return;
            }
            StringBuilder world_string = new StringBuilder("Options: ");
            for (World world : Bukkit.getWorlds()) {
                world_string.append(world.getName()).append(", ");
            }
            commandSender.sendMessage(world_string.substring(0, world_string.length() - 2));
            return;
        }
        StringBuilder path_builder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            path_builder.append(args[i]);
            path_builder.append(" ");
        }
        String path_string = path_builder.substring(0, path_builder.length() - 1);
        if (unloadWorld(path_string)) {
            commandSender.sendMessage("World Saved!");
            return;
        }
        for (World world : Bukkit.getWorlds()) {
            if (world.getName().contains(path_string)) {
                if (unloadWorld(world.getName())) {
                    commandSender.sendMessage("World Saved!");
                    return;
                }
            }
        }
        commandSender.sendMessage("Could not find world specified.");
    }

    public static boolean unloadWorld(String name) {
        World world = Bukkit.getWorld(name);
        if (world == null) {
            return false;
        }
        for (Player player : world.getPlayers()) {
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        }
        Bukkit.unloadWorld(world, true);
        return true;
    }

    public static void searchTeleportWorld(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof Player)) {
            return;
        }
        Player player = (Player) commandSender;
        if (args.length < 2) {
            listWorlds(commandSender);
            return;
        }
        teleportWorld(player, args[1]);
        commandSender.sendMessage("Sending to World: " + player.getWorld().getName());
        return;
    }

    public static void teleportWorld(Player player, String check) {
        // Check for exact names first
        for (World world : Bukkit.getWorlds()) {
            if (world.getName().equalsIgnoreCase(check)) {
                player.teleport(world.getSpawnLocation());
                return;
            }
        }
        // Check for matched names
        for (World world : Bukkit.getWorlds()) {
            if (world.getName().toLowerCase().contains(check.toLowerCase())) {
                player.teleport(world.getSpawnLocation());
                return;
            }
        }
    }

    public static void convertWorld(CommandSender commandSender) {
        if (!(commandSender instanceof Player)) {
            return;
        }
        commandSender.sendMessage("Converting...");
        Player player = (Player) commandSender;
        World world = player.getWorld();
        Block center = world.getSpawnLocation().getBlock();
        // Parse map for other objects
        int parse_radius = 150;
        for (int x = -parse_radius; x <= parse_radius; x++) {
            for (int y = -parse_radius; y <= parse_radius; y++) {
                for (int z = -parse_radius; z <= parse_radius; z++) {
                    Block parsed = center.getRelative(x, y, z);
                    if(parsed == null) {
                        continue;
                    }
                    if(isCenterPoint(parsed) || isRespawnPoint(parsed) || isBoundaryPoint(parsed)) {
                        // Remove all existing item frames on the block
                        for(Entity entity : parsed.getWorld().getNearbyEntities(parsed.getLocation().add(0.5, 0.5, 0.5), 1, 1, 1)) {
                            if(entity instanceof ItemFrame) {
                                entity.remove();
                            }
                        }
                        // For some reason existing item frames take a while to remove so this has to be delayed
                        Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                ItemFrame frame = world.spawn(parsed.getRelative(BlockFace.SOUTH).getLocation(), ItemFrame.class);
                                frame.setFacingDirection(BlockFace.SOUTH);
                            }
                        }, 40L);
                    }
                }
            }
        }
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> ((Player) commandSender).sendMessage("Finished Converting."), 50L);
    }

    public static boolean isRespawnPoint(Block check) {
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

    public static boolean isBoundaryPoint(Block check) {
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

    public static boolean isCenterPoint(Block check) {
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

}
