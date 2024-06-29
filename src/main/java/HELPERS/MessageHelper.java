package HELPERS;

public class MessageHelper {
    public static final String CHOOSE_WORD = "CHOOSE_WORD";
    public static final String GUESS_WORD = "GUESS_WORD";
    public static final String GAME_STATUS = "GAME_STATUS";
    public static final String GAME_OVER = "GAME_OVER";
    public static final String CLOSE = "CLOSE";

    private String type;
    private String message;

    public MessageHelper(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public String serialize() {
        return this.type + ";" + this.message;
    }

    public static MessageHelper deserialize(String serializedMessage) {
        String[] parts = serializedMessage.split(";", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Zły format");
        }
        return new MessageHelper(parts[0], parts[1]);
    }
}