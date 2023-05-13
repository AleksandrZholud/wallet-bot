package telegrambot.model.enums;

public enum DraftStatus {
    BUILDING("building..."),
    BUILT("built..."),
    SAVING("saving..."),
    SAVED("saved...");

    String statusName;

    DraftStatus(String statusName) {
        this.statusName = statusName;
    }
}
