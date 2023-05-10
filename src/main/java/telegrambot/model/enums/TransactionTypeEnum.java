package telegrambot.model.enums;

import java.util.Arrays;

public enum TransactionTypeEnum {

    INCOME("Income"),

    EXPENSE("Expense");

    private final String name;

    TransactionTypeEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static TransactionTypeEnum getByName(String name) throws IllegalStateException {
        return Arrays.stream(TransactionTypeEnum.values())
                .filter(value -> value.name.equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Command not found: " + name));
    }

}
