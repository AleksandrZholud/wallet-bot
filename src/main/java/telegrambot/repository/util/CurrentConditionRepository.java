package telegrambot.repository.util;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import telegrambot.model.util.CurrentCondition;

@Repository
public interface CurrentConditionRepository extends JpaRepository<CurrentCondition, Long> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE current_condition SET state_id =:stateId, command_id =:commandId", nativeQuery = true)
    int updateCommandAndState(@Param("commandId") Long commandId,
                              @Param("stateId") Long stateId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE current_condition SET command_id =:commandId", nativeQuery = true)
    int updateCommand(@Param("commandId") Long commandId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE current_condition SET state_id =:stateId", nativeQuery = true)
    int updateState(@Param("stateId") Long stateId);

    @Query(value = "SELECT c.id,c.command_id,c.state_id FROM current_condition AS c LIMIT 1", nativeQuery = true)
    CurrentCondition getCurrentCondition();

    @Transactional
    @Modifying
    @Query(value = "    DELETE FROM current_condition;                                                     "
            + "         INSERT INTO current_condition (id, command_id, state_id)                           "
            + "         VALUES (1, (SELECT id FROM commands c WHERE c.name = '/start'),                    "
            + "                    (SELECT id FROM states s   WHERE s.name = 'NoState' ) )                 "
            , nativeQuery = true)
    void reset();

    @Query(value = "    SELECT CASE                                                                        "
            + "                  WHEN cc.state_id = (SELECT id FROM states s WHERE s.name = 'NoState')     "
            + "                      THEN 0                                                                "
            + "                  ELSE csd.previous_state_id                                                "
            + "                END                                                                         "
            + "         FROM current_condition cc                                                          "
            + "         JOIN command_state_dependency csd ON csd.current_state_id = cc.state_id            "
            + "                                          AND csd.command_id = cc.command_id                "
            , nativeQuery = true)
    long getPreviousStateId();
}