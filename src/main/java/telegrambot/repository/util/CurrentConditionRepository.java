package telegrambot.repository.util;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import telegrambot.model.util.CurrentCondition;
import telegrambot.model.util.State;

import java.util.Optional;

@Repository
public interface CurrentConditionRepository extends JpaRepository<CurrentCondition, Long> {

    @Query(value = "SELECT c.id,c.command_id,c.state_id FROM current_condition AS c LIMIT 1", nativeQuery = true)
    Optional<CurrentCondition> getCurrentConditionOptional();

    @Query(value = "    SELECT CASE                                                                        "
            + "                  WHEN cc.state_id = (SELECT id FROM states s WHERE s.name = 'NoState')     "
            + "                      THEN 0                                                                "
            + "                  ELSE csd.previous_state_id                                                "
            + "                END                                                                         "
            + "         FROM current_condition cc                                                          "
            + "         JOIN command_state_dependency csd ON csd.current_state_id = cc.state_id            "
            + "                                          AND csd.command_id = cc.command_id                "
            , nativeQuery = true)
    Optional<State> getPreviousStateOptional();

    @Transactional
    @Modifying
    @Query(value = "    DELETE FROM current_condition;                                                     "
            + "         INSERT INTO current_condition (id, command_id, state_id)                           "
            + "         VALUES (1, (SELECT id FROM commands c WHERE c.name = '/start'),                    "
            + "                    (SELECT id FROM states s   WHERE s.name = 'NoState' ) )                 "
            , nativeQuery = true)
    void reset();
}