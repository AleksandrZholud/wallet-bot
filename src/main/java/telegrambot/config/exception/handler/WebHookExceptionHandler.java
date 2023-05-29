package telegrambot.config.exception.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegrambot.config.exception.DatabaseOperationException;
import telegrambot.config.interceptor.UserDataContextHolder;
import telegrambot.config.telegram.TelegramWalletBot;

@ControllerAdvice
@RequiredArgsConstructor
public class WebHookExceptionHandler extends ResponseEntityExceptionHandler {

    private final TelegramWalletBot telegramWalletBot;

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Void> handleIllegalStateException(IllegalStateException ex, WebRequest request) throws TelegramApiException {
        UserDataContextHolder
                .getFacade()
                .addStartButton()
                .setText(ex.getMessage());
        telegramWalletBot.execute(UserDataContextHolder.performMessage());
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(DatabaseOperationException.class)
    public ResponseEntity<Void> handleDatabaseOperationException(DatabaseOperationException ex, WebRequest request) throws TelegramApiException {
        UserDataContextHolder
                .getFacade()
                .addStartButton()
                .setText(ex.getMessage());
        telegramWalletBot.execute(UserDataContextHolder.performMessage());
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> unhandledExceptionHandler(Exception ex, WebRequest request) throws TelegramApiException {
        UserDataContextHolder
                .getFacade()
                .addStartButton()
                .setText(ex.getMessage());
        telegramWalletBot.execute(UserDataContextHolder.performMessage());
        return ResponseEntity.ok().build();
    }
}