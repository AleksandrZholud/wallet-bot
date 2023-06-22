package telegrambot.repository.util;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import telegrambot.model.util.State;

import java.util.Optional;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {

    @Query(value = "     SELECT id, name                                                                   "
            + "          FROM states                                                                       "
            + "          WHERE name=:name                                                                  "
            , nativeQuery = true)
    Optional<State> findByNameOptional(@Param("name") String name);

    @Query(value = "    WITH t (id) AS                                                                     "
            + "         (SELECT CASE                                                                       "
            + "                   WHEN cc.state_id = (SELECT id FROM states s WHERE s.name = 'NoState')    "
            + "                       THEN 0                                                               "
            + "                   ELSE csd.previous_state_id                                               "
            + "                 END                                                                        "
            + "         FROM current_condition cc                                                          "
            + "         JOIN command_state_dependency csd ON csd.current_state_id = cc.state_id            "
            + "                                          AND csd.command_id = cc.command_id)               "
            + "         SELECT id,name                                                                     "
            + "         FROM states                                                                        "
            + "         WHERE id=(SELECT id FROM t LIMIT 1)                                                "
            , nativeQuery = true)
    Optional<State> getPreviousStateOptional();
}