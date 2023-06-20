package telegrambot.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "telegram-update-validation-properties")
public class TelegramUpdateValidationProperties {

    @NotBlank
    private Boolean enabled;
}
