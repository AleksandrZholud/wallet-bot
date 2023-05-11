package telegrambot.execurors;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class AbstractCommandExecutor {

    /**
     * private static final String THIS_CMD - must be defined in every child. THIS_CMD means command related to this exact child.
     */

    private static final List<AbstractCommandExecutor> allChildEntities = new ArrayList<>();

    AbstractCommandExecutor() {
        allChildEntities.add(this);
    }

    public static List<AbstractCommandExecutor> getAllChildEntities() {
        return new ArrayList<>(allChildEntities);
    }

    public static <T extends AbstractCommandExecutor> T getSpecificChild(Class<T> clazz) {
        return allChildEntities.stream()
                .filter(o -> o.getClass().equals(clazz))
                .map(clazz::cast)
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Cannot find an impl bean of StartCmdHandler named: " + clazz.getSimpleName());
                    return new IllegalStateException("Cannot find a bean");
                });
    }

    public abstract boolean isSystemHandler();

    /**
     * Retrieve last message from DB
     * Remove last msg from DB and set current State to previous
     * Fails with default SendMessage if current condition wasn`t set, or if last msg wasn`t removed
     */
    public abstract void processMessage() throws IllegalAccessException;

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