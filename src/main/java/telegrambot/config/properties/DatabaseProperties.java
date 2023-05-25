package telegrambot.config.properties;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.sql.DataSource;
import javax.validation.constraints.NotBlank;

@Validated
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "db-properties")
public class DatabaseProperties {

    @NotBlank
    private String jdbcProvider;

    @NotBlank
    private String host;

    @NotBlank
    private String port;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String dbName;

    protected String getJdbcUrl() {
        return String.format("jdbc:%s://%s:%s/%s", jdbcProvider, host, port, dbName);
    }

    public DataSource getNewDataSource(String dbName) {
        this.dbName = dbName;

        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setJdbcUrl(getJdbcUrl());
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        dataSource.setConnectionTimeout(3000);
        dataSource.setIdleTimeout(10000);
        dataSource.setMaxLifetime(30000);

        dataSource.setMinimumIdle(3);
        dataSource.setMaximumPoolSize(8);

        return dataSource;
    }
}