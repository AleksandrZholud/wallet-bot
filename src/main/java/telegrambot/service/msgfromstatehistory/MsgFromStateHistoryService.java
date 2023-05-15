package telegrambot.service.msgfromstatehistory;

import telegrambot.model.util.MsgFromStateHistory;

public interface MsgFromStateHistoryService {

    public void deleteAll();

    public MsgFromStateHistory save(MsgFromStateHistory msgFromStateHistory);
}
