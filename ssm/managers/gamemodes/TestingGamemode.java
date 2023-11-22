package ssm.managers.gamemodes;

import ssm.events.GameStateChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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
    public void setPlayerLives(HashMap<Player, Integer> lives) {
        for(Player player : server.players) {
            if (server.isSpectator(player)) {
                continue;
            }
            lives.put(player, 99);
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
