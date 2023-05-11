package telegrambot.repository.util;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import telegrambot.model.util.MsgFromStateHistory;

@Repository
public interface MsgFromStateHistoryRepository extends JpaRepository<MsgFromStateHistory, Long> {

    @Query(value = "      SELECT id, message, timestamp                                                "
            + "           FROM command_state_message_history                                           "
            + "           WHERE timestamp =                                                            "
            + "                 (                                                                      "
            + "                     SELECT max(timestamp)                                              "
            + "                     FROM command_state_message_history                                 "
            + "                 )                                                                      ",
            nativeQuery = true)
    MsgFromStateHistory findLast();

    @Query(value = "     SELECT message                                                                "
            + "          FROM command_state_message_history                                            "
            + "          ORDER BY timestamp DESC                                                       "
            + "          LIMIT 1 OFFSET 1                                                              ",
            nativeQuery = true)
    String findPreLast();

    @Transactional
    @Modifying
    @Query(value = "      DELETE FROM command_state_message_history WHERE id = :id                     ",
            nativeQuery = true)
    int removeById(@Param("id") Long id);


    @Transactional
    @Modifying
    @Query(value = "      DELETE FROM command_state_message_history                                    "
            + "           WHERE timestamp =                                                            "
            + "         ( SELECT max(timestamp) FROM command_state_message_history )                   ",
            nativeQuery = true)
    int removeLast();
}