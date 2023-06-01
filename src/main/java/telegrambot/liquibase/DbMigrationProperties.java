package telegrambot.liquibase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DbMigrationProperties {

    private String dbName;
    private List<String> labels;
    private String tag;
}