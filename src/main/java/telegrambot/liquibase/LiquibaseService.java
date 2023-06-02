package telegrambot.liquibase;

import java.util.List;

public interface LiquibaseService {

    boolean getDbStatus(String dbMigrationProperties);

    String getDbUpdateSql(String dbName, List<String> labels);

    boolean updateDb(DbMigrationProperties dbMigrationProperties);
}