package telegrambot.service.state_history;

import telegrambot.model.util.MsgFromStateHistory;

public interface MsgFromStateHistoryService {

    void deleteAll();

    void save(MsgFromStateHistory msgFromStateHistory);

    boolean isEmpty();

    String findPreLast();

    void removeLast();

}
