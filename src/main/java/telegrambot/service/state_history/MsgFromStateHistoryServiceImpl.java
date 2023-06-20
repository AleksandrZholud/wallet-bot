package telegrambot.service.state_history;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import telegrambot.model.util.MsgFromStateHistory;
import telegrambot.repository.util.MsgFromStateHistoryRepository;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Component
public class MsgFromStateHistoryServiceImpl implements MsgFromStateHistoryService {

    private final MsgFromStateHistoryRepository msgFromStateHistoryRepository;

    @Override
    public MsgFromStateHistory save(MsgFromStateHistory msgFromStateHistory) {
        return msgFromStateHistoryRepository.save(msgFromStateHistory);
    }

    @Override
    public boolean isEmpty() {
        Optional<MsgFromStateHistory> optionalMsgFromStateHistory = msgFromStateHistoryRepository.getLastOptional();
        return optionalMsgFromStateHistory.isEmpty();
    }

    @Override
    public MsgFromStateHistory getPreLast() {
        return msgFromStateHistoryRepository.getPreLast()
                .orElseThrow(() -> new IllegalStateException("No PreLast message in Command-State message history"));
    }

    @Override
    public void removeLast() {
        if (msgFromStateHistoryRepository.removeLast() == 0) {
            log.warn("Method 'removeLast()' of MsgFromStateHistoryRepository.class didn`t remove any messages from Command-State message history table");
        }
    }

    @Override
    public void deleteAll() {
        msgFromStateHistoryRepository.deleteAll();
    }
}
