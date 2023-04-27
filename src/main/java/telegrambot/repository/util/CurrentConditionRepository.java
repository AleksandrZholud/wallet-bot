package telegrambot.repository.util;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import telegrambot.model.util.Command;
import telegrambot.model.util.CurrentCondition;
import telegrambot.model.util.State;

@Repository
public interface CurrentConditionRepository extends JpaRepository<CurrentCondition, Long> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE current_condition SET stateid = :stateId, commandid = :commandId", nativeQuery = true)
    int updateCommandAndState(@Param("commandId") Long commandId,
                              @Param("stateId") Long stateId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE current_condition SET commandid = :commandId", nativeQuery = true)
    int updateCommand(@Param("commandId") Long commandId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE current_condition SET stateid = :stateId", nativeQuery = true)
    int updateState(@Param("stateId") Long stateId);

    @Transactional
    @Query(value = "SELECT c.id,c.commandid,c.stateid FROM current_condition AS c LIMIT 1", nativeQuery = true)
    CurrentCondition getFirst();

}