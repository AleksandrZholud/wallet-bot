package telegrambot.model.enums;

public enum DRAFT_STATUS {
    BUILDING("building..."),
    BUILT("built..."),
    SAVING("saving..."),
    SAVED("saved...");

    String statusName;

    DRAFT_STATUS(String statusName) {
        this.statusName = statusName;
    }
}
