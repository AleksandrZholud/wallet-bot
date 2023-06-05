package telegrambot.service.migration;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import telegrambot.liquibase.DbMigrationProperties;
import telegrambot.liquibase.LiquibaseService;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DbMigrationServiceImpl implements DbMigrationService {

    private final LiquibaseService liquibaseService;

    @Override
    public boolean getDbStatus(String dbName, DataSource dataSource) {
        return liquibaseService.getDbStatus(dbName, dataSource);
    }

    @Override
    public String getDbUpdateSql(String dbName, List<String> labels, DataSource dataSource) {
        return liquibaseService.getDbUpdateSql(dbName, labels, dataSource);
    }

    @Async
    @Override
    public void updateDb(DbMigrationProperties dbMigrationProperties, DataSource dataSource) {
        boolean updated = liquibaseService.updateDb(dbMigrationProperties, dataSource);
        if (!updated) {
            throw new IllegalStateException("Error during update DB: " + dbMigrationProperties.getDbName());
        }
    }

    @Async
    @Override
    public void updateAllDb(DataSource dataSource) {
        List<DbMigrationProperties> allExistedDbNames = new ArrayList<>(); //TODO: Andrey
        for (DbMigrationProperties dbMigrationProperties : allExistedDbNames) {
            liquibaseService.updateDb(dbMigrationProperties, dataSource);
        }
    }
}
