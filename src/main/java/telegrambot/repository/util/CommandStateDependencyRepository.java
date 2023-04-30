package telegrambot.repository.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import telegrambot.model.util.Command;
import telegrambot.model.util.CommandStateDependency;
import telegrambot.model.util.State;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommandStateDependencyRepository extends JpaRepository<CommandStateDependency, Long> {

    @Query("select c from CommandStateDependency c where c.commandId = ?1 and c.currentState=?2")
    Optional<CommandStateDependency> findByCurrentState(Command commandId, State currentState);

    @Query("SELECT csd FROM CommandStateDependency csd WHERE csd.commandId = :command ORDER BY csd.id ASC")
    List<CommandStateDependency> findByCommand(@Param("command") Command command, Pageable pageable);

    @Query(value = " SELECT id, commandid, baseid, currentstateid, nextstateid, previousstateid "
            + "      FROM command_state_dependency                                              "
            + "      WHERE commandid = :currentCommand AND currentstateid = :currentState       "
            + "      LIMIT 1                                                                    ",
            nativeQuery = true)
    CommandStateDependency findByCurCommandAndCurSate(@Param("currentCommand") Command currentCommand,
                                                      @Param("currentState") State currentState);

    default CommandStateDependency findByCommandIdFirstPage(Command command) {
        List<CommandStateDependency> dependencies = findByCommand(command, PageRequest.of(0, 1));
        return dependencies.isEmpty() ? null : dependencies.get(0);
    }

}