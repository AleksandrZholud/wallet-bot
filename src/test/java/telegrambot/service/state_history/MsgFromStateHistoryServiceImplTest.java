package telegrambot.service.state_history;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import telegrambot.model.util.MsgFromStateHistory;
import telegrambot.repository.util.MsgFromStateHistoryRepository;

import java.sql.Timestamp;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest(classes = MsgFromStateHistoryServiceImpl.class)
class MsgFromStateHistoryServiceImplTest {

    @Autowired
    MsgFromStateHistoryService msgFromStateHistoryService;

    @MockBean
    MsgFromStateHistoryRepository msgFromStateHistoryRepository;

    @Test
    void deleteAll() {

        //before
        doNothing().when(msgFromStateHistoryRepository).deleteAll();

        //when
        msgFromStateHistoryService.deleteAll();

        //then
        verify(msgFromStateHistoryRepository).deleteAll();
    }

    @Test
    void save() {

        //before
        Timestamp timestamp = new Timestamp(232323L);
        MsgFromStateHistory msgFromStateHistory = new MsgFromStateHistory(1L, "SetBalance", timestamp);

        when(msgFromStateHistoryRepository.save(msgFromStateHistory)).thenReturn(msgFromStateHistory);

        //when
        msgFromStateHistoryService.save(msgFromStateHistory);

        //then
        verify(msgFromStateHistoryRepository).save(msgFromStateHistory);
    }

    @Test
    void isEmpty() {

        //before
        Timestamp timestamp = new Timestamp(232323L);
        MsgFromStateHistory expectedRes = new MsgFromStateHistory(1L, "SetBalance", timestamp);
        Optional<MsgFromStateHistory> optionalMsgFromStateHistory = Optional.of(expectedRes);

        when(msgFromStateHistoryRepository.getLastOptional()).thenReturn(optionalMsgFromStateHistory);

        //when
        Boolean actualRes = msgFromStateHistoryService.isEmpty();

        //then
        assertThat(actualRes)
                .isNotNull();
        verify(msgFromStateHistoryRepository).getLastOptional();
    }

    @Test
    void findPreLast() {

        //before
        MsgFromStateHistory msg = MsgFromStateHistory.builder()
                .id(1L)
                .message("MSG")
                .timestamp(new Timestamp(5L))
                .build();

        when(msgFromStateHistoryRepository.getPreLast()).thenReturn(Optional.of(msg));

        //when
        var actualRes = msgFromStateHistoryService.getPreLast();

        //then
        assertThat(actualRes)
                .isNotNull()
                .isEqualTo(msg);
        verify(msgFromStateHistoryRepository).getPreLast();
    }
    @Test
    void findPreLast_Ex() {

        //before
        MsgFromStateHistory msg = MsgFromStateHistory.builder()
                .id(1L)
                .message("MSG")
                .timestamp(new Timestamp(5L))
                .build();

        when(msgFromStateHistoryRepository.getPreLast()).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(()->msgFromStateHistoryService.getPreLast())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No PreLast message in Command-State message history");
        verify(msgFromStateHistoryRepository).getPreLast();
    }

    @Test
    void removeLast() {

        //before
        when(msgFromStateHistoryRepository.removeLast()).thenReturn(1);

        //when
        msgFromStateHistoryService.removeLast();

        //then
        verify(msgFromStateHistoryRepository).removeLast();
    }
}