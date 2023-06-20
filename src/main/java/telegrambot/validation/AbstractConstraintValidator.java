package telegrambot.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.Set;

public abstract class AbstractConstraintValidator implements Validator {

    private final ValidatorFactory validatorFactory;

    public AbstractConstraintValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
    }

    @Override
    public void validate(Object target, Errors errors) {
        checkAnnotationConstraintViolations(target, errors);
        checkSpecialConstraintViolations(target, errors);
    }

    protected void checkAnnotationConstraintViolations(Object target, Errors errors) {
        Set<ConstraintViolation<Object>> violations = validatorFactory.getValidator().validate(target);

        violations.forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.rejectValue(propertyPath, "", message);
        });
    }

    protected abstract void checkSpecialConstraintViolations(Object target, Errors errors);
}
