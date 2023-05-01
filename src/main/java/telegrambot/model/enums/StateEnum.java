package telegrambot.model.enums;

import java.util.Arrays;

public enum StateEnum {
    NO_STATE("NoState", "NoState"),
    SET_NAME("SetName", "SetName"),
    SET_BALANCE("SetBalance", "SetBalance"),
    SET_AMOUNT("SetAmount", "SetAmount"),
    SET_TYPE("SetType", "SetType"),
    CHOOSE_CARD("ChooseCard", "ChooseCard"),
    SET_DATE_FROM("SetDateFrom", "SetDateFrom"),
    SET_DATE_TO("SetDateTo", "SetDateTo"),
    CONFIRMATION("Confirmation", "Confirmation");

    private final String state;
    private final String buttonText;

    StateEnum(String state, String buttonText) {
        this.state = state;
        this.buttonText = buttonText;
    }

    public String getState() {
        return state;
    }

    public String getButtonText() {
        return buttonText;
    }

    public static StateEnum findByState(String state) throws IllegalStateException {
        return Arrays.stream(StateEnum.values())
                .filter(s -> s.state.equals(state))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("State not found: " + state));
    }
}
