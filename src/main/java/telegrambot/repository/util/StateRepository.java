package telegrambot.repository.util;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import telegrambot.model.util.State;

@Repository
public interface StateRepository extends JpaRepository<State, Long> {
    State findByName(String name);
}