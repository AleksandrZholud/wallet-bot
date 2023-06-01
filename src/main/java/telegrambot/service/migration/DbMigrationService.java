package telegrambot.service.migration;

import telegrambot.liquibase.DbMigrationProperties;

import java.util.List;

public interface DbMigrationService {
    boolean getDbStatus(String dbName);

    String getDbUpdateSql(String dbName, List<String> labels);

    void updateDb(DbMigrationProperties dbName);

    void updateAllDb();
}