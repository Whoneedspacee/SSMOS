package SSM.Utilities;

public enum ServerMessageType {
    GAME("§9Game>§7"),
    RECHARGE("§9Recharge>§7"),
    DEATH("§9Death>§7"),
    SKILL("§9Skill>§7");

    private String message;

    ServerMessageType(String message) {
        this.message = message;
    }

    public String toString() {
        return message;
    }

}
