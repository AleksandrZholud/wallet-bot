package telegrambot.model.enums;

public enum TransactionTypeEnum {

    INCOME("Income"),

    EXPENSE("Expense");

    private final String name;

    TransactionTypeEnum(String name) {
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
}
