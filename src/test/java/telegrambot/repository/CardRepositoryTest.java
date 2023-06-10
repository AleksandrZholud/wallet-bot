package telegrambot.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import telegrambot.model.Card;

import java.math.BigDecimal;
import java.util.Optional;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CardRepositoryTest {

    @Autowired
    private CardRepository cardRepository;

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:14.3-alpine")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("sql/init.sql");

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Test
    public void testGetByName() {
        // Before
        cardRepository.save(Card.builder()
                .name("John Doe")
                .balance(BigDecimal.valueOf(1000))
                .build());

        // When
        Optional<Card> foundCard = cardRepository.getByName("John Doe");

        // Then
        Assertions.assertTrue(foundCard.isPresent());
        Assertions.assertEquals("John Doe", foundCard.get().getName());
        Assertions.assertEquals(BigDecimal.valueOf(1000), foundCard.get().getBalance());
    }
}