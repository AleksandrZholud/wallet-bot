package telegrambot.liquibase;

import javax.sql.DataSource;
import java.util.List;

public interface LiquibaseService {

    boolean getDbStatus(String dbMigrationProperties, DataSource dataSource);

    String getDbUpdateSql(String dbName, List<String> labels, DataSource dataSource);

    boolean updateDb(DbMigrationProperties dbMigrationProperties, DataSource dataSource);
}