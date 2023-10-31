package SSM.GameManagers.Gamemodes;

import SSM.GameManagers.GameManager;

public class TeamsGamemode extends SmashGamemode {

    public TeamsGamemode() {
        super();
        this.name = "Super Smash Mobs Teams";
        this.description = new String[] {
                "Each player has 3 respawns",
                "Attack to restore hunger!",
                "Last player alive wins!"
        };
    }

    // Add solo maps to teams automatically
    @Override
    public void updateAllowedMaps() {
        super.updateAllowedMaps();
        allowed_maps.addAll(GameManager.all_gamemodes.get(0).getAllowedMaps());
    }

}
