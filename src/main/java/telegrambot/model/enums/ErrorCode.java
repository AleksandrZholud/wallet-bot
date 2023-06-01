package telegrambot.model.enums;

public enum ErrorCode {
    GENERAL_ERROR(1),
    TOO_LONG_RESPONSE_ERROR(2);

    public Integer code;

    private ErrorCode(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return this.code;
    }
}
