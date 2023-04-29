package telegrambot.model.util;

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
