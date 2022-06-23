package telegrambot.model;

public enum OPERATION_TYPE {
    INCOME("INCOME"),
    EXPENDITURE("EXPENDITURE");

    String type;

    OPERATION_TYPE(String input){
        this.type = input;
    }

    public String toString() {
        return type;
    }
}
