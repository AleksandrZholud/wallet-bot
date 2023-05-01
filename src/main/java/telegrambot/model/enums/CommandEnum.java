package telegrambot.model.enums;

import java.util.Arrays;

public enum CommandEnum {
    CREATE_CARD_COMMAND("/CreateCard", "Create card"),
    CREATE_CARD_CONFIRM_COMMAND("/CreateCardConfirm", "Confirm"),
    START_COMMAND("/Start", "Start"),
    GO_BACK_COMMAND("/Back", "Back");

    private final String command;
    private final String buttonText;

    CommandEnum(String command, String buttonText) {
        this.command = command;
        this.buttonText = buttonText;
    }

    public String getCommand() {
        return command;
    }

    public String getButtonText() {
        return buttonText;
    }

    public static CommandEnum findByCommand(String command) throws IllegalStateException {
        return Arrays.stream(CommandEnum.values())
                .filter(value -> value.command.equals(command))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Command not found: " + command));
    }
}