package telegrambot.service.msgfromstatehistory;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import telegrambot.model.util.MsgFromStateHistory;
import telegrambot.repository.util.MsgFromStateHistoryRepository;

@AllArgsConstructor
@Component

public class MsgFromStateHistoryServiceImpl implements MsgFromStateHistoryService {

    private final MsgFromStateHistoryRepository msgFromStateHistoryRepository;

    @Override
    public void deleteAll() {
        msgFromStateHistoryRepository.deleteAll();
    }

    @Override
    public MsgFromStateHistory save(MsgFromStateHistory msgFromStateHistory) {
        return msgFromStateHistoryRepository.save(msgFromStateHistory);
    }
}