package ssm.gamemanagers.gamemodes;

import ssm.events.GameStateChangeEvent;
import ssm.gamemanagers.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.HashMap;

public class TestingGamemode extends SmashGamemode {

    public TestingGamemode() {
        super();
        this.name = "Testing";
        this.description = new String[] {
                "Each player has 3 respawns",
                "Attack to restore hunger!",
                "Last player alive wins!"
        };
        this.players_to_start = 1;
    }

    // Don't create a folder for the testing gamemodes maps
    @Override
    public void updateAllowedMaps() {
        allowed_maps = GameManager.all_gamemodes.get(0).getAllowedMaps();
    }

    @Override
    public void setPlayerLives(HashMap<Player, Integer> lives) {
        for(Player player : GameManager.getPlayers()) {
            if (GameManager.isSpectator(player)) {
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
        GameManager.setTimeLeft(0);
    }

}
