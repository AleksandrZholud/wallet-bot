package telegrambot.config.hibernate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import telegrambot.config.properties.DatabaseProperties;
import telegrambot.repository.DefaultRepository;

import javax.sql.DataSource;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DatabaseController {

    private final DatabaseProperties databaseProperties;
    private final LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean;
    private final DefaultRepository defaultRepository;

    @Transactional
    @GetMapping("/switch-database")
    public String switchDatabase() {

        long l = System.currentTimeMillis();

        DataSource newDataSource1 = (databaseProperties.getNewDataSource("temp_db"));
        DataSource newDataSource2 = (databaseProperties.getNewDataSource("user_597668797"));

        for (int i = 0; i < 6; i++) {
            long step = System.currentTimeMillis();
            localContainerEntityManagerFactoryBean.setDataSource(newDataSource1);
            //localContainerEntityManagerFactoryBean.afterPropertiesSet();

            defaultRepository.insertSomething("Hello " + i++);

            localContainerEntityManagerFactoryBean.setDataSource(newDataSource2);
            //localContainerEntityManagerFactoryBean.afterPropertiesSet();

            defaultRepository.insertSomething("Hello " + i);

            log.info("Step in: " + (System.currentTimeMillis() - step) / 1000.0 + " sec.");
        }

        return "Successfully switched to new database in: " + (System.currentTimeMillis() - l) / 1000.0 + " sec.";
    }
}