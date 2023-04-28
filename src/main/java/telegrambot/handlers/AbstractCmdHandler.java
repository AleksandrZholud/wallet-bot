package telegrambot.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCmdHandler {
    private static final List<AbstractCmdHandler> allChildEntities = new ArrayList<>();

    //must implement in every child!
    private static final String THIS_CMD = "/chindCommand"; //TODO: ← is it really norm name?

    AbstractCmdHandler() {
        allChildEntities.add(this);
    }

    public static List<AbstractCmdHandler> getAllChildEntities() {
        return allChildEntities;
    }

    public abstract SendMessage processMessage() throws IllegalAccessException;

    public abstract boolean canProcessMessage();
}