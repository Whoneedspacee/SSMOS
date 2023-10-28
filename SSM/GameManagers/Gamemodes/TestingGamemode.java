package SSM.GameManagers.Gamemodes;

import SSM.GameManagers.GameManager;
import SSM.GameManagers.Maps.MapFile;

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

}
