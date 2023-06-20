package telegrambot.config.telegram;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Validated
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "telegram-bot")
public class BotConfig {

    @NotEmpty
    private String name;

    @NotEmpty
    private String token;

    @NotEmpty
    private String webhookPath;
}