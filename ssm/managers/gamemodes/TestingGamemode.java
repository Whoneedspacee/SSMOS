package ssm.managers.gamemodes;

import ssm.events.GameStateChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import ssm.kits.Kit;
import ssm.kits.boss.KitElderGuardian;
import ssm.kits.boss.KitWitherBoss;
import ssm.kits.ssmos.OSKitBlaze;
import ssm.kits.ssmos.OSKitSnowman;
import ssm.managers.BossBarManager;
import ssm.managers.KitManager;
import ssm.managers.gamestate.GameState;
import ssm.managers.smashserver.SmashServer;

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
