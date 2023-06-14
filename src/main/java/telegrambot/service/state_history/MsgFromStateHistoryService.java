package telegrambot.service.state_history;

import telegrambot.model.util.MsgFromStateHistory;

public interface MsgFromStateHistoryService {

    void deleteAll();

    MsgFromStateHistory save(MsgFromStateHistory msgFromStateHistory);

    boolean isEmpty();

    MsgFromStateHistory getPreLast();

    void removeLast();

}
