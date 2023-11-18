package ssm.managers.gamestate;

public class GameState {

    public static final short LOBBY_WAITING = 0;
    public static final short LOBBY_VOTING = 1;
    public static final short LOBBY_STARTING = 2;
    public static final short GAME_STARTING = 3;
    public static final short GAME_PLAYING = 4;
    public static final short GAME_ENDING = 5;

    public static String toString(short state) {
        switch (state) {
            case LOBBY_WAITING -> {
                return "Lobby Waiting";
            }
            case LOBBY_VOTING -> {
                return "Lobby Voting";
            }
            case LOBBY_STARTING -> {
                return "Lobby Starting";
            }
            case GAME_STARTING -> {
                return "Game Starting";
            }
            case GAME_PLAYING -> {
                return "Game Playing";
            }
            case GAME_ENDING -> {
                return "Game Ending";
            }
        }
        return "";
    }

    public static boolean isStarting(short given_state) {
        return (given_state == GameState.GAME_STARTING);
    }

    public static boolean isPlaying(short given_state) {
        return (given_state == GameState.GAME_PLAYING);
    }

}
