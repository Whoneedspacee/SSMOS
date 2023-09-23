package SSM.Utilities;

public enum ServerMessageType {
    GAME("§9Game>"),
    RECHARGE("§9Recharge>"),
    DEATH("§9Death>"),
    SKILL("§9Skill>");

    private String message;

    ServerMessageType(String message) {
        this.message = message;
    }

    public String toString() {
        return message;
    }

}
