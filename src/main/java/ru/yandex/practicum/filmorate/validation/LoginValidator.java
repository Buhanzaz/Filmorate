package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.CorrectLogin;

public class LoginValidator implements ConstraintValidator<CorrectLogin, String> {
    @Override
    public boolean isValid(String loginValidator, ConstraintValidatorContext constraintValidatorContext) {
        if (loginValidator == null) {
            return true;
        }
        return !(loginValidator.contains(" "));
    }
}
