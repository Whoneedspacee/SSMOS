package xyz.whoneedspacee.ssmos.managers.gamemodes;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.whoneedspacee.ssmos.managers.*;
import xyz.whoneedspacee.ssmos.managers.gamestate.GameState;
import xyz.whoneedspacee.ssmos.managers.smashmenu.SmashMenu;
import xyz.whoneedspacee.ssmos.managers.smashteam.SmashTeam;
import xyz.whoneedspacee.ssmos.Main;
import xyz.whoneedspacee.ssmos.events.SmashDamageEvent;
import xyz.whoneedspacee.ssmos.kits.Kit;
import xyz.whoneedspacee.ssmos.kits.boss.BossKitData;
import xyz.whoneedspacee.ssmos.kits.boss.KitElderGuardian;
import xyz.whoneedspacee.ssmos.kits.boss.KitWitherBoss;
import xyz.whoneedspacee.ssmos.managers.smashscoreboard.SmashScoreboard;
import xyz.whoneedspacee.ssmos.managers.smashserver.SmashServer;
import xyz.whoneedspacee.ssmos.utilities.ServerMessageType;
import xyz.whoneedspacee.ssmos.utilities.Utils;

import java.util.*;

public class BossGamemode extends SmashGamemode {

    private Player selected_boss = null;
    private SmashTeam boss_team = TeamManager.createTeam("Boss", ChatColor.RED);
    private SmashTeam players_team = TeamManager.createTeam("Players", ChatColor.YELLOW);
    private HashMap<Player, Double> boss_damage = new HashMap<Player, Double>();
    private Player first_damage = null;
    private Player second_damage = null;
    private Player third_damage = null;
    protected List<Kit> allowed_bosses = new ArrayList<Kit>();

    public BossGamemode() {
        super();
        this.name = "SSM Boss Fight";
        this.short_name = "BOSS";
        this.description = new String[]{
                "Each player has 3 respawns",
                "Attack to restore hunger!",
                "Defeat the boss to win!"
        };
        this.players_to_start = 2;
        this.max_players = 10;
    }

    @Override
    public void updateAllowedKits() {
        super.updateAllowedKits();
        allowed_bosses.clear();
        allowed_bosses.add(new KitElderGuardian());
        allowed_bosses.add(new KitWitherBoss());
    }

    @Override
    public void setPlayerLobbyItems(Player player) {
        super.setPlayerLobbyItems(player);
        player.getInventory().setItem(2, Main.SELECT_BOSS_ITEM);
    }

    // Select one boss player and put all players on teams
    @Override
    public void setPlayerLives(HashMap<Player, Integer> lives) {
        boss_damage.clear();
        boss_team.removeAllPlayers();
        players_team.removeAllPlayers();
        List<Player> possible_boss = new ArrayList<Player>();
        for (Player player : server.players) {
            if (server.isSpectator(player)) {
                continue;
            }
            lives.put(player, 4);
            players_team.addPlayer(player);
            possible_boss.add(player);
        }
        int random_player = (int) (Math.random() * possible_boss.size());
        selected_boss = possible_boss.get(random_player);
        lives.put(selected_boss, 1);
        players_team.removePlayer(selected_boss);
        boss_team.addPlayer(selected_boss);
    }

    // Set the kit of the boss player
    @Override
    public void setPlayerKit(Player player) {
        if (player.equals(selected_boss)) {
            Kit chosen_kit = getDefaultBossKit(player);
            if(chosen_kit == null) {
                chosen_kit = allowed_bosses.get((int) (Math.random() * allowed_bosses.size()));
            }
            KitManager.equipPlayer(player, chosen_kit);
            Kit equipped_kit = KitManager.getPlayerKit(player);
            double health_per_player = 200;
            if(equipped_kit instanceof BossKitData) {
                BossKitData bossKitData = (BossKitData) equipped_kit;
                health_per_player = bossKitData.getHealthPerPlayer();
            }
            if(equipped_kit != null) {
                equipped_kit.setArmor(Utils.getArmorForExactHP((server.getActivePlayerCount() - 1) * health_per_player));
            }
            return;
        }
        super.setPlayerKit(player);
    }

    // Display boss first and display health percent
    @Override
    public List<String> getLivesScoreboard() {
        List<String> scoreboard_string = new ArrayList<String>();
        for (Player player : players_team.getPlayersSortedByLives()) {
            scoreboard_string.add(server.getLives(player) + " " + SmashScoreboard.getPlayerColor(player, true) + player.getName());
        }
        scoreboard_string.add(ChatColor.WHITE + "" + ChatColor.STRIKETHROUGH + "--------------------");
        for (Player player : boss_team.getPlayersSortedByLives()) {
            double damage_multiplier = 1;
            Kit kit = KitManager.getPlayerKit(player);
            if (kit != null) {
                damage_multiplier = (1 - 0.08 * kit.getArmor());
            }
            scoreboard_string.add(String.format("%d", (int) Math.round(player.getHealth() / damage_multiplier)) + ChatColor.RED +
                    " ❤ " + ChatColor.RESET + SmashScoreboard.getPlayerColor(player, true) + player.getName());
        }
        return scoreboard_string;
    }

    @Override
    public void update() {
        if (server.getState() >= GameState.GAME_STARTING && server.getState() < GameState.GAME_ENDING) {
            String boss_name = "Smash Boss";
            if (KitManager.getPlayerKit(selected_boss) != null) {
                boss_name = KitManager.getPlayerKit(selected_boss).getName();
            }
            for (Player player : server.players) {
                BossBarManager.updateBar(player, boss_name, 100);
                BossBarManager.teleportBar(player);
            }
        } else if (server.getState() == GameState.GAME_ENDING) {
            for (Player player : server.players) {
                BossBarManager.removeBar(player);
                boss_team.removeAllPlayers();
                players_team.removeAllPlayers();
            }
        }
    }

    @Override
    public void afterGameEnded(Player player) {
        if(player.equals(first_damage) || player.equals(second_damage) || player.equals(third_damage)) {
            return;
        }
        if(!boss_damage.containsKey(player)) {
            return;
        }
        Utils.sendServerMessageToPlayer("You did" + ChatColor.RED + " ❤ " + ChatColor.RESET +
                String.format("%d", boss_damage.get(player).intValue()), player, ServerMessageType.GAME);
    }

    @Override
    public boolean isGameEnded(HashMap<Player, Integer> lives) {
        return !boss_team.hasAliveMembers() || !players_team.hasAliveMembers();
    }

    @Override
    public String getFirstPlaceString() {
        for(Player player : server.players) {
            if(server.isSpectator(player)) {
                continue;
            }
            if(player.equals(selected_boss)) {
                continue;
            }
            boss_damage.putIfAbsent(player, 0.0);
        }
        Player max_damage = null;
        for (Player player : boss_damage.keySet()) {
            if (max_damage == null || boss_damage.get(player) > boss_damage.get(max_damage)) {
                max_damage = player;
            }
        }
        first_damage = max_damage;
        if(max_damage == null) {
            return null;
        }
        return max_damage.getName() + ChatColor.RED + " ❤ " + ChatColor.RESET + String.format("%d", boss_damage.get(max_damage).intValue());
    }

    @Override
    public String getSecondPlaceString() {
        Player max_damage = null;
        for (Player player : boss_damage.keySet()) {
            if(player.equals(first_damage)) {
                continue;
            }
            if (max_damage == null || boss_damage.get(player) > boss_damage.get(max_damage)) {
                max_damage = player;
            }
        }
        second_damage = max_damage;
        if(max_damage == null) {
            return null;
        }
        return max_damage.getName() + ChatColor.RED + " ❤ " + ChatColor.RESET + String.format("%d", boss_damage.get(max_damage).intValue());
    }

    @Override
    public String getThirdPlaceString() {
        Player max_damage = null;
        for (Player player : boss_damage.keySet()) {
            if(player.equals(first_damage)) {
                continue;
            }
            if(player.equals(second_damage)) {
                continue;
            }
            if (max_damage == null || boss_damage.get(player) > boss_damage.get(max_damage)) {
                max_damage = player;
            }
        }
        third_damage = max_damage;
        if(max_damage == null) {
            return null;
        }
        return max_damage.getName() + ChatColor.RED + " ❤ " + ChatColor.RESET + String.format("%d", boss_damage.get(max_damage).intValue());
    }

    public void setDefaultBossKit(Player player, Kit kit) {
        Utils.sendServerMessageToPlayer("Set " + ChatColor.GREEN + kit.getName() +
                ChatColor.GRAY + " as your default boss kit.", player, ServerMessageType.GAME);
        ConfigManager.setPlayerGamemodeConfigOption(player, ConfigManager.ConfigOption.BOSS_KIT, kit.getName(), this);
    }

    public Kit getDefaultBossKit(Player player) {
        Object config_option = ConfigManager.getPlayerGamemodeConfigOption(player, ConfigManager.ConfigOption.BOSS_KIT, this);
        if (config_option == null) {
            return null;
        }
        String default_kit_name = config_option.toString();
        for (Kit kit : allowed_bosses) {
            if (kit.getName().equals(default_kit_name)) {
                return kit;
            }
        }
        return null;
    }

    public void openBossKitMenu(Player player) {
        SmashServer server = GameManager.getPlayerServer(player);
        if(server == null || !server.getCurrentGamemode().equals(this)) {
            return;
        }
        int slot = 10;
        int count = 0;
        List<Kit> allowed_kits = allowed_bosses;
        Inventory selectKit = Bukkit.createInventory(player, (int) (9 * (Math.ceil(allowed_kits.size() / 7.0) + 2)), "Choose a Kit");
        SmashMenu menu = MenuManager.createPlayerMenu(player, selectKit);
        for (Kit kit : allowed_kits) {
            ItemStack item = kit.getMenuItemStack();
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(ChatColor.RESET + kit.getName());
            item.setItemMeta(itemMeta);
            selectKit.setItem(slot, item);
            menu.setActionFromSlot(slot, (e) -> {
                if(e.getWhoClicked() instanceof Player) {
                    Player clicked = (Player) e.getWhoClicked();
                    if(server.getState() <= GameState.LOBBY_STARTING) {
                        setDefaultBossKit(player, kit);
                        //KitManager.equipPlayer(player, kit);
                    }
                    clicked.playSound(clicked.getLocation(), Sound.ORB_PICKUP, 1f, 1f);
                    clicked.closeInventory();
                }
            });
            slot++;
            count++;

            if (count % 7 == 0) {
                slot += 2;
            }
        }
        player.openInventory(selectKit);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void addBossDamage(SmashDamageEvent e) {
        if (e.isCancelled()) {
            return;
        }
        if (selected_boss == null || !selected_boss.equals(e.getDamagee())) {
            return;
        }
        LivingEntity damager = e.getDamager();
        if(damager == null) {
            damager = DamageManager.getLastDamageEvent(selected_boss).getDamager();
        }
        if (!(damager instanceof Player)) {
            return;
        }
        double real_damage = e.getDamage();
        if(e.getIgnoreArmor()) {
            double damage_multplier = 1;
            Kit kit = KitManager.getPlayerKit(selected_boss);
            if (kit != null) {
                damage_multplier = (1 - 0.08 * kit.getArmor());
            }
            real_damage = e.getDamage() / damage_multplier;
        }
        Player player = (Player) damager;
        boss_damage.putIfAbsent(player, 0.0);
        boss_damage.put(player, boss_damage.get(player) + real_damage);
    }

}
