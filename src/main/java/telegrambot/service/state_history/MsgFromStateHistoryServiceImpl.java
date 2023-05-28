package telegrambot.service.state_history;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import telegrambot.model.util.MsgFromStateHistory;
import telegrambot.repository.util.MsgFromStateHistoryRepository;

import java.util.Optional;

@AllArgsConstructor
@Component
public class MsgFromStateHistoryServiceImpl implements MsgFromStateHistoryService {

    private final MsgFromStateHistoryRepository msgFromStateHistoryRepository;

    @Override
    public void deleteAll() {
        msgFromStateHistoryRepository.deleteAll();
    }

    @Override
    public void save(MsgFromStateHistory msgFromStateHistory) {
        msgFromStateHistoryRepository.save(msgFromStateHistory);
    }

    @Override
    public boolean isEmpty() {
        Optional<MsgFromStateHistory> optionalMsgFromStateHistory = msgFromStateHistoryRepository.findLast();
        return optionalMsgFromStateHistory.isEmpty();
    }

    @Override
    public String findPreLast() {
        return msgFromStateHistoryRepository.findPreLast();
    }

    @Override
    public void removeLast() {
        msgFromStateHistoryRepository.removeLast();
    }
}