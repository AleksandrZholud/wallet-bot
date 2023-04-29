package telegrambot.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCmdHandler {

    /**
     * private static final String THIS_CMD - must be defined in every child. THIS_CMD means command related to this exact child.
     */

    private static final List<AbstractCmdHandler> allChildEntities = new ArrayList<>();
    AbstractCmdHandler() {
        allChildEntities.add(this);
    }

    public static List<AbstractCmdHandler> getAllChildEntities() {
        return allChildEntities;
    }

    public abstract SendMessage processMessage() throws IllegalAccessException;

    public abstract boolean canProcessMessage();
}