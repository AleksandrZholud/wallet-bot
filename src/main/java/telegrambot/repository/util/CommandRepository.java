package telegrambot.repository.util;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import telegrambot.model.util.Command;

@Repository
public interface CommandRepository extends JpaRepository<Command, Long> {

    @Query(value = "SELECT c.id,c.name FROM commands AS c WHERE c.name =:name", nativeQuery = true)
    Command findByName(@Param("name") String name);
}