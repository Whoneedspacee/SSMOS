package SSM.GameManagers.Gamemodes;

import SSM.Events.GameStateChangeEvent;
import SSM.GameManagers.GameManager;
import SSM.GameManagers.Maps.MapFile;
import SSM.Kits.KitIronGolem;
import SSM.Kits.KitSkeleton;
import SSM.Kits.KitSpider;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity;
import org.bukkit.event.EventHandler;

import java.io.File;

public class TestingGamemode extends SmashGamemode {

    public TestingGamemode() {
        super();
        this.name = "Testing";
        this.description = new String[] {
                "Each player has 3 respawns",
                "Attack to restore hunger!",
                "Last player alive wins!"
        };
    }

    // Don't create a folder for the testing gamemodes maps
    @Override
    public void updateAllowedMaps() {
        allowed_maps = GameManager.all_gamemodes.get(0).getAllowedMaps();
    }

    @Override
    public void updateAllowedKits() {
        allowed_kits.add(new KitSkeleton());
        allowed_kits.add(new KitIronGolem());
        allowed_kits.add(new KitSpider());
    }

    @EventHandler
    public void onGameStateChanged(GameStateChangeEvent e) {
        if(!isCurrentGamemode()) {
            return;
        }
        GameManager.setTimeLeft(0);
    }

}
