package telegrambot.config.exception.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import telegrambot.config.exception.DatabaseOperationException;
import telegrambot.config.exception.TelegramUpdateValidationException;
import telegrambot.config.interceptor.UserDataContextHolder;
import telegrambot.config.multitenancy.TenantManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ControllerAdvice
@RequiredArgsConstructor
public class WebHookExceptionHandler extends ResponseEntityExceptionHandler {

    private final TenantManager tenantManager;

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<SendMessage> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        UserDataContextHolder
                .getFacade()
                .addStartButton()
                .setText(ex.getMessage());
        if (ex.getMessage().contains("Error during update DB")) {
            tenantManager.dropConnection(extractUserDbName(ex.getMessage()));
        }
        return ResponseEntity.ok(UserDataContextHolder.performMessage());
    }

    @ExceptionHandler(DatabaseOperationException.class)
    public ResponseEntity<SendMessage> handleDatabaseOperationException(DatabaseOperationException ex, WebRequest request) {
        UserDataContextHolder
                .getFacade()
                .addStartButton()
                .setText(ex.getMessage());
        return ResponseEntity.ok(UserDataContextHolder.performMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SendMessage> unhandledExceptionHandler(Exception ex, WebRequest request) {
        UserDataContextHolder
                .getFacade()
                .addStartButton()
                .setText(ex.getMessage());
        return ResponseEntity.ok(UserDataContextHolder.performMessage());
    }

    @ExceptionHandler(TelegramUpdateValidationException.class)
    public ResponseEntity<SendMessage> myExceptionHandler(TelegramUpdateValidationException ex, WebRequest request) {
        UserDataContextHolder
                .getFacade()
                .addStartButton()
                .setText("Server error.");
        return ResponseEntity.ok(UserDataContextHolder.performMessage());
    }

    private String extractUserDbName(String inputString) {
        String pattern = "user_\\d+";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(inputString);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return null;
        }
    }
}
