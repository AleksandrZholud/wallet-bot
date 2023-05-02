package telegrambot.config.telegram;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Validated
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "telegram-bot")
public class BotConfig {

    @NotNull
    @NotEmpty
    String name;

    @NotNull
    @NotEmpty
    String token;
}