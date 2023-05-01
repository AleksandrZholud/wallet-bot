package telegrambot.repository.util;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import telegrambot.model.util.Command;
import telegrambot.model.util.CommandStateDependency;
import telegrambot.model.util.State;

@Repository
public interface CommandStateDependencyRepository extends JpaRepository<CommandStateDependency, Long> {

    @Query(value = " SELECT csd.id, csd.command_id, csd.base_id, csd.current_state_id, csd.next_state_id, csd.previous_state_id"
            + "      FROM command_state_dependency csd                                                "
            + "      WHERE csd.command_id =:currentCommand AND csd.current_state_id = :currentState       "
            + "      LIMIT 1                                                                    ",
            nativeQuery = true)
    CommandStateDependency findByCurCommandAndCurSate(@Param("currentCommand") Command currentCommand,
                                                      @Param("currentState") State currentState);


}