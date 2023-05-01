package telegrambot.handlers;

import org.springframework.data.annotation.Immutable;
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
        return new ArrayList<>(allChildEntities);
    }

    public abstract SendMessage processMessage() throws IllegalAccessException;

    public abstract boolean canProcessMessage();

    /**
     * Must clean and check if all handlerData cleaned
     * handlerData is all tables that where involved to help only this handler, and didn't touch anything else
     * Check all tables after cleaning and return boolean
     * This method should not touch currentCondition!
     *
     * @return true if oll tables has been cleaned
     */
    public abstract boolean cleanAllData();

}