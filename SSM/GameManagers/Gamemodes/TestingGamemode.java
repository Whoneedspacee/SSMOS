package SSM.GameManagers.Gamemodes;

import SSM.Events.GameStateChangeEvent;
import SSM.GameManagers.GameManager;
import SSM.GameManagers.Maps.MapFile;
import SSM.Kits.KitIronGolem;
import SSM.Kits.KitSkeleton;
import SSM.Kits.KitSpider;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.io.File;
import java.util.HashMap;
import java.util.List;

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

    /*@Override
    public void updateAllowedKits() {
        allowed_kits.add(new KitSkeleton());
        allowed_kits.add(new KitIronGolem());
        allowed_kits.add(new KitSpider());
    }*/

    @Override
    public void setPlayerLives(HashMap<Player, Integer> lives) {
        for(Player player : GameManager.getPlayers()) {
            lives.put(player, 99);
        }
    }

    @Override
    public boolean isGameEnded(HashMap<Player, Integer> lives) {
        return (lives.size() <= 0);
    }

    @EventHandler
    public void onGameStateChanged(GameStateChangeEvent e) {
        if(!isCurrentGamemode()) {
            return;
        }
        GameManager.setTimeLeft(0);
    }

}
