package telegrambot.config.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import telegrambot.config.exception.DatabaseOperationException;
import telegrambot.config.exception.LongResponseException;
import telegrambot.config.interceptor.UserDataContextHolder;

@ControllerAdvice
public class WebHookExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<SendMessage> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        UserDataContextHolder
                .getFacade()
                .addStartButton()
                .setText(ex.getMessage());
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

    @ExceptionHandler(LongResponseException.class)
    public ResponseEntity<SendMessage> myExceptionHandler(LongResponseException ex, WebRequest request) {
        return ResponseEntity.ok().build();
    }
}