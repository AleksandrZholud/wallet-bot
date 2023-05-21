package telegrambot.service.msgfromstatehistory;

import telegrambot.model.util.MsgFromStateHistory;

public interface MsgFromStateHistoryService {

    void deleteAll();

    void save(MsgFromStateHistory msgFromStateHistory);

    MsgFromStateHistory findLast();

    String findPreLast();

    void removeLast();

}
