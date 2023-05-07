package telegrambot.model.enums;

import java.util.Arrays;

public enum StateEnum {
    NO_STATE("NoState"),
    SET_NAME("SetName"),
    SET_BALANCE("SetBalance"),
    SET_AMOUNT("SetAmount"),
    SET_TYPE("SetType"),
    CHOOSE_CARD("ChooseCard"),
    SET_DATE_FROM("SetDateFrom"),
    SET_DATE_TO("SetDateTo"),
    CONFIRMATION("Confirmation");

    private final String state;

    StateEnum(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public static StateEnum findByState(String state) throws IllegalStateException {
        return Arrays.stream(StateEnum.values())
                .filter(s -> s.state.equals(state))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("State not found: " + state));
    }
}
