package telegrambot.repository.util;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import telegrambot.model.util.CurrentCondition;

import java.util.Optional;

@Repository
public interface CurrentConditionRepository extends JpaRepository<CurrentCondition, Long> {

    @Query(value = "    SELECT c.id,c.command_id,c.state_id                                  "
            + "         FROM current_condition AS c LIMIT 1                                  "
            , nativeQuery = true)
    Optional<CurrentCondition> getCurrentConditionOptional();

    @Transactional
    @Modifying
    @Query(value = "    DELETE FROM current_condition where id > 0;                          "
            + "         INSERT INTO current_condition (id, command_id, state_id)             "
            + "         VALUES (1, (SELECT id FROM commands c WHERE c.name = '/start'),      "
            + "                    (SELECT id FROM states s   WHERE s.name = 'NoState' ) )   "
            , nativeQuery = true)
    void reset();
}