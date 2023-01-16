package ru.practicum.shareit.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckNotBlankIfNotNullValidator implements ConstraintValidator<NotBlankIfNotNull, CharSequence> {


    @Override
    public void initialize(NotBlankIfNotNull constraintAnnotation) {
    }

    @Override
    public boolean isValid(CharSequence charSequence, ConstraintValidatorContext constraintValidatorContext) {
        if (charSequence == null) {
            return true;
        } else {
            return charSequence.toString().trim().length() > 0;
        }
    }
}
