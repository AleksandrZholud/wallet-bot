package telegrambot.liquibase;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.changelog.ChangeSet;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.SearchPathResourceAccessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import telegrambot.config.exception.DatabaseOperationException;

import javax.sql.DataSource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(LiquibaseProperties.class)
public class LiquibaseServiceImpl implements LiquibaseService {

    public static final String UPDATE_SQL_COMMAND_TEMPLATE = "update-sql --changelog-file=";

    private final DataSource dataSource;
    private final LiquibaseProperties liquibaseProperties;

    @Override
    public boolean getDbStatus(String dbName) {

        try (Connection connection = dataSource.getConnection()) {
            connection.setCatalog(dbName);
            try (Liquibase liquibase
                         = new Liquibase(liquibaseProperties.getChangeLog(),
                    new ClassLoaderResourceAccessor(),
                    new JdbcConnection(connection))) {

                List<ChangeSet> sets = liquibase.listUnrunChangeSets(new Contexts(), new LabelExpression());

                return sets.isEmpty();
            }
        } catch (Exception e) {
            log.error("Error getting status for database: " + dbName, e);
            throw new DatabaseOperationException("Error getting status for database: " + dbName);
        }
    }

    @Override
    public String getDbUpdateSql(String dbName, List<String> labels) {

        try (Connection connection = dataSource.getConnection()) {
            connection.setCatalog(dbName);
            try (Liquibase liquibase
                         = new Liquibase(liquibaseProperties.getChangeLog(),
                    new ClassLoaderResourceAccessor(),
                    new JdbcConnection(connection))) {

                String command = UPDATE_SQL_COMMAND_TEMPLATE + liquibaseProperties.getChangeLog();
                StringWriter stringWriter = new StringWriter();
                liquibase.update(command,
                        new Contexts(),
                        new LabelExpression(labels),
                        stringWriter);

                return stringWriter.toString();
            }
        } catch (Exception e) {
            log.error("Error applying update-sql for database: " + dbName, e);
            throw new DatabaseOperationException("Error applying update-sql for database: " + dbName);
        }
    }

    @Override
    public boolean updateDb(DbMigrationProperties dbMigrationProperties) {
        String dbName = dbMigrationProperties.getDbName();

        try (Connection connection = dataSource.getConnection()) {
            connection.setCatalog(dbName);
            try (Liquibase liquibase
                         = new Liquibase(liquibaseProperties.getChangeLog(),
                    new ClassLoaderResourceAccessor(),
                    new JdbcConnection(connection))) {

                log.info("Updating database: {}", dbName);

                liquibase.update(new Contexts(),
                        new LabelExpression(dbMigrationProperties.getLabels()));

                liquibase.tag(dbMigrationProperties.getTag());
                return true;
            }
        } catch (Exception e) {
            log.error("Error updating database: " + dbName, e);
        }
        return false;
    }

    private static class FileReader {
        public List<String> readLinesFromClasspath(String filePath) {
            List<String> lines = null;
            // Получение InputStream из класспаса
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);

            if (inputStream != null) {
                // Чтение содержимого файла из InputStream
                try (Scanner scanner = new Scanner(inputStream)) {
                    lines = new ArrayList<>();
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        lines.add(line);
                    }
                }
            }
            return lines;
        }
    }
}