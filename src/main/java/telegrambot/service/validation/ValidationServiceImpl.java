package telegrambot.service.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;
import telegrambot.config.exception.TelegramUpdateValidationException;
import telegrambot.config.properties.TelegramUpdateValidationProperties;
import telegrambot.service.message.MessageService;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;
import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {


    private final List<Validator> validators;
    private final MessageService messageService;
    private final TelegramUpdateValidationProperties validationProperties;

    @Override
    public void validate(Object obj) throws TelegramUpdateValidationException {

        if (TRUE.equals(validationProperties.getEnabled())) {

            Validator targetValidator = validators.stream()
                    .filter(validator -> validator.supports(obj.getClass()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            format("No validator that support object with type: %s", obj.getClass().getName())));

            var dataBinder = new DataBinder(obj);
            dataBinder.addValidators(targetValidator);
            dataBinder.validate();

            if (dataBinder.getBindingResult().hasErrors()) {
                var errors = dataBinder.getBindingResult().getAllErrors().stream()
                        .map(this::resolveErrorMessage)
                        .collect(Collectors.joining(","));

                throw new TelegramUpdateValidationException(errors);
            }
        }
    }

    private String resolveErrorMessage(ObjectError error) {

        String defaultMessage = error.getDefaultMessage();
        String message = defaultMessage;

        if (defaultMessage != null && !defaultMessage.isEmpty()) {
            if (error instanceof FieldError) {
                FieldError fieldError = (FieldError) error;
                message = messageService.getMessage("common.field.error", fieldError.getField(), fieldError.getDefaultMessage());
            }
        } else {
            message = messageService.getMessage(error.getCode(), error.getArguments());
        }

        return message;
    }
}
