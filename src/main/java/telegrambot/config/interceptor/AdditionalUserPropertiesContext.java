package telegrambot.config.interceptor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Update;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalUserPropertiesContext {
    private Update update;
}