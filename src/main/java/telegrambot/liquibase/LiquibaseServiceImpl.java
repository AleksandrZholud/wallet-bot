package telegrambot.liquibase;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.changelog.ChangeSet;
import liquibase.database.jvm.JdbcConnection;
import liquibase.integration.spring.SpringLiquibase;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import telegrambot.config.exception.DatabaseOperationException;

import javax.sql.DataSource;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(LiquibaseProperties.class)
public class LiquibaseServiceImpl implements LiquibaseService {

    public static final String UPDATE_SQL_COMMAND_TEMPLATE = "update-sql --changelog-file=";

    private final LiquibaseProperties liquibaseProperties;
    private final ResourceLoader resourceLoader;

    @Override
    public boolean getDbStatus(String dbName, DataSource dataSource) {

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
    public String getDbUpdateSql(String dbName, List<String> labels, DataSource dataSource) {

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
    public boolean updateDb(DbMigrationProperties dbMigrationProperties, DataSource dataSource) {
        String dbName = dbMigrationProperties.getDbName();

        try {
            SpringLiquibase springLiquibase = new SpringLiquibase();
            springLiquibase.setDataSource(dataSource);
            springLiquibase.setChangeLog(liquibaseProperties.getChangeLog());
            springLiquibase.setResourceLoader(resourceLoader);
            springLiquibase.setContexts(String.valueOf(new Contexts()));
            springLiquibase.setLabels(dbMigrationProperties.getLabels().toString());
            springLiquibase.setTag(dbMigrationProperties.getTag());

            log.info("Updating database: {}", dbName);

            springLiquibase.afterPropertiesSet();

            return true;
        } catch (Exception e) {
            log.error("Error updating database: " + dbName, e);
            return false;
        }
    }
}