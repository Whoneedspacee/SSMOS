package SSM.GameManagers.Gamemodes;

public class TeamsGamemode extends SmashGamemode {

    public TeamsGamemode() {
        super();
        this.name = "Super Smash Mobs Teams";
        this.description = new String[] {
                "Each player has 3 respawns and team members",
                "Attack to restore hunger!",
                "Last player alive wins!"
        };
    }

}
