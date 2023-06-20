package telegrambot.service.migration;

import telegrambot.liquibase.DbMigrationProperties;

import javax.sql.DataSource;
import java.util.List;

public interface DbMigrationService {
    boolean getDbStatus(String dbName, DataSource dataSource);

    String getDbUpdateSql(String dbName, List<String> labels, DataSource dataSource);

    void updateDb(DbMigrationProperties dbName, DataSource dataSource);

    void updateAllDb(DataSource dataSource);
}