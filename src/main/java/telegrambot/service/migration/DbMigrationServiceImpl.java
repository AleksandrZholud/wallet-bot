package telegrambot.service.migration;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import telegrambot.liquibase.DbMigrationProperties;
import telegrambot.liquibase.LiquibaseService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DbMigrationServiceImpl implements DbMigrationService {

    private final LiquibaseService liquibaseService;

    @Override
    public boolean getDbStatus(String dbName) {
        return liquibaseService.getDbStatus(dbName);
    }

    @Override
    public String getDbUpdateSql(String dbName, List<String> labels) {
        return liquibaseService.getDbUpdateSql(dbName, labels);
    }

    @Async
    @Override
    public void updateDb(DbMigrationProperties dbMigrationProperties) {
        boolean updated = liquibaseService.updateDb(dbMigrationProperties);
        if (!updated){
            throw new IllegalStateException("Error during update DB: " + dbMigrationProperties.getDbName());
        }
    }

    @Async
    @Override
    public void updateAllDb() {
        List<DbMigrationProperties> allExistedDbNames = new ArrayList<>(); //TODO: Andrey
        for (DbMigrationProperties dbMigrationProperties : allExistedDbNames) {
            liquibaseService.updateDb(dbMigrationProperties);
        }
    }
}
