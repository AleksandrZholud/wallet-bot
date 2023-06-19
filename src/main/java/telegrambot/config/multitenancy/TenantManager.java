package telegrambot.config.multitenancy;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Component;
import telegrambot.config.exception.DatabaseOperationException;
import telegrambot.config.properties.DataSourceConfiguration;
import telegrambot.liquibase.DbMigrationProperties;
import telegrambot.service.migration.DbMigrationService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyList;
import static telegrambot.util.ConsoleColors.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class TenantManager {

    private final DataSource dataSource;
    private final DbMigrationService migrationService;
    private final DataSourceConfiguration dataSourceConfiguration;
    private final LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean;

    public static final String ERROR_CREATING_DB = "Creating a new db error.";
    public static final String USER_DB_NAME_SUFFIX = "user_";

    private Map<String, DataSource> dataSourceMap = new HashMap<>();

    public void switchDataSource(Long userId, String userName) {
        log.info("Trying to switch db for user: {}", userName);

        switchDataSource(userId.toString(), true);
    }

    public void switchDataSource(String dbName, boolean isUserDb) {
        validateDbName(dbName);

        if (isUserDb) {
            dbName = USER_DB_NAME_SUFFIX + dbName;
        }
        if (dataSourceMap.containsKey(dbName)) {
            log.info(YELLOW_BOLD + "Ok, the connection to db '{}' already exists." + RESET, dbName);
            doSwitchConnection(dbName, dataSourceMap.get(dbName));
        } else {
            log.info(YELLOW_BOLD + "Connection to db: '{}' not exist" + RESET, dbName);
            doCreateDbAndConnect(dbName);
        }
    }

    private void doSwitchConnection(String dbName, DataSource existedDataSource) {
        log.trace(CYAN_BACKGROUND + BLACK_BOLD + "Switching connection..." + RESET);
        localContainerEntityManagerFactoryBean.setDataSource(existedDataSource);
        localContainerEntityManagerFactoryBean.afterPropertiesSet();
        log.trace(CYAN_BACKGROUND + BLACK_BOLD + "Switched connection successfully." + RESET);

        migrate(dbName, existedDataSource);
    }

    private void validateDbName(String dbName) {
        if (dbName.toUpperCase().contains("DROP") || dbName.toUpperCase().contains("DELETE")) {
            throw new IllegalStateException("Error: dbName contains system word 'DROP' or 'DELETE'.");
        }
    }

    private void doCreateDbAndConnect(String dbName) {
        log.trace(CYAN_BACKGROUND + BLACK_BOLD + "Trying to create DB..." + RESET);
        createDb(dbName);

        log.trace(CYAN_BACKGROUND + BLACK_BOLD + "Creating connection for DB: '{}'" + RESET, dbName);
        HikariDataSource newDataSource = dataSourceConfiguration.getNewDataSource(dbName);
        dataSourceMap.put(dbName, newDataSource);

        localContainerEntityManagerFactoryBean.setDataSource(newDataSource);
        localContainerEntityManagerFactoryBean.afterPropertiesSet();
        log.info(YELLOW_BOLD + "Connected successfully." + RESET);

        migrate(dbName, newDataSource);
    }

    private void migrate(String dbName, DataSource newDataSource) {
        log.trace(CYAN_BACKGROUND + BLACK_BOLD + "Starting migration..." + RESET);
        DbMigrationProperties migrationProperties = new DbMigrationProperties(dbName, emptyList(), "default-setup");
        migrationService.updateDb(migrationProperties, newDataSource);
        log.info(YELLOW_BOLD + "Migration successfully done for DB: {}" + RESET, dbName);
    }

    private void createDb(String dbName) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()
        ) {
            String createDatabaseQuery = "CREATE DATABASE " + dbName;
            statement.executeUpdate(createDatabaseQuery);

            log.info(YELLOW_BOLD + "DB successfully created." + RESET);
        } catch (SQLException e) {
            if (e.getMessage().toLowerCase().contains("already exists")) {
                log.info(YELLOW_BOLD + "'{}' already exists" + RESET, dbName);
            } else {
                throw new DatabaseOperationException(ERROR_CREATING_DB);
            }
        }
    }
}