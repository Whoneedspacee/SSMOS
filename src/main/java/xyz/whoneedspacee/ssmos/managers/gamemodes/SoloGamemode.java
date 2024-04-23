package xyz.whoneedspacee.ssmos.managers.gamemodes;

public class SoloGamemode extends SmashGamemode {

    public SoloGamemode() {
        super();
        this.name = "Super Smash Mobs";
        this.short_name = "SSM";
        this.description = new String[] {
                "Each player has 3 respawns",
                "Attack to restore hunger!",
                "Last player alive wins!"
        };
    }

}
