package xyz.whoneedspacee.ssmos.managers.gamemodes;

import xyz.whoneedspacee.ssmos.managers.gamestate.GameState;
import xyz.whoneedspacee.ssmos.events.GameStateChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import xyz.whoneedspacee.ssmos.kits.Kit;
import xyz.whoneedspacee.ssmos.kits.boss.KitElderGuardian;
import xyz.whoneedspacee.ssmos.kits.boss.KitWitherBoss;
import xyz.whoneedspacee.ssmos.kits.ssmos.OSKitBlaze;
import xyz.whoneedspacee.ssmos.kits.ssmos.OSKitIllusioner;
import xyz.whoneedspacee.ssmos.kits.ssmos.OSKitSnowman;
import xyz.whoneedspacee.ssmos.managers.KitManager;

import java.util.HashMap;

public class TestingGamemode extends SmashGamemode {

    public TestingGamemode() {
        super();
        this.name = "Testing";
        this.short_name = "TEST";
        this.description = new String[] {
                "Each player has 3 respawns",
                "Attack to restore hunger!",
                "Last player alive wins!"
        };
        this.players_to_start = 1;
        this.max_players = 99;
    }

    @Override
    public void updateAllowedKits() {
        super.updateAllowedKits();
        allowed_kits.add(new KitElderGuardian());
        allowed_kits.add(new KitWitherBoss());
        allowed_kits.add(new OSKitBlaze());
        allowed_kits.add(new OSKitSnowman());
        allowed_kits.add(new OSKitIllusioner());
    }

    @Override
    public void setPlayerLives(HashMap<Player, Integer> lives) {
        for(Player player : server.players) {
            if (server.isSpectator(player)) {
                continue;
            }
            lives.put(player, 99);
        }
    }

    @Override
    public void update() {
        if (server.getState() == GameState.GAME_PLAYING) {
            for (Player player : server.players) {
                server.lives.put(player, 99);
                Kit kit = KitManager.getPlayerKit(player);
                if(kit == null) {
                    KitManager.equipPlayer(player, allowed_kits.get(0));
                }
            }
        }
    }

    @Override
    public boolean isGameEnded(HashMap<Player, Integer> lives) {
        return (lives.size() <= 0);
    }

    @EventHandler
    public void onGameStateChanged(GameStateChangeEvent e) {
        if(!e.getServer().equals(server)) {
            return;
        }
        server.setTimeLeft(0);
    }

}
