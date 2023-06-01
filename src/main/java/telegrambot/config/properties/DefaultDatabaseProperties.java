package telegrambot.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Validated
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "db-properties")
public class DefaultDatabaseProperties {

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

    protected String getJdbcUrl(String dbName) {
        return String.format("jdbc:%s://%s:%s/%s", jdbcProvider, host, port, dbName);
    }
}