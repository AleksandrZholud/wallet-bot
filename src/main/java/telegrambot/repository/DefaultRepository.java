package telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import telegrambot.model.DefaultTable;

// TODO: 29.05.2023 Zholud: remove this class
public interface DefaultRepository extends JpaRepository<DefaultTable, Long> {

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO default_table(message) VALUES (:hi)", nativeQuery = true)
    void insertSomething(@Param("hi") String message);
}