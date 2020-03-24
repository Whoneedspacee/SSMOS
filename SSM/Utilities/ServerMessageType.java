package SSM.Utilities;

public enum ServerMessageType {
    GAME("§9Game>"),
    RECHARGE("§9Recharge>"),
    DEATH("§9Death>");

    private String message;

    ServerMessageType(String message) {
        this.message = message;
    }

    public String toString() {
        return message;
    }

}
