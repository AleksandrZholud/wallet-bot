package telegrambot.model.enums;

import java.util.Arrays;

public enum CommandEnum {
    CREATE_CARD_COMMAND("Create a card", true),
    CREATE_TRANSACTION_COMMAND("Add income/expense", true),
    CREATE_CARD_CONFIRM_COMMAND("Confirm creating card", false),
    CREATE_TRANSACTION_CONFIRM_COMMAND("Confirm transaction", false),
    START_COMMAND("/start", false),
    LEFT_COMMAND("←", false),
    RIGHT_COMMAND("→", false),
    GO_BACK_COMMAND("Back", false);

    private final String command;
    private final boolean isShownWhenStart;

    CommandEnum(String command, boolean isShownWhenStart) {
        this.command = command;
        this.isShownWhenStart = isShownWhenStart;
    }

    public String getCommand() {
        return command;
    }

    public static CommandEnum findByCommand(String command) throws IllegalStateException {
        return Arrays.stream(CommandEnum.values())
                .filter(value -> value.command.equals(command))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Command not found: " + command));
    }

    public static CommandEnum[] getGlobalCommands(){
        return Arrays.stream(CommandEnum.values())
                .filter(value -> value.isShownWhenStart)
                .toArray(CommandEnum[]::new);
    }
}