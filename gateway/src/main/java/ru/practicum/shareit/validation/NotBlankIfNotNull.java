package ru.practicum.shareit.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(ElementType.FIELD)
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = CheckNotBlankIfNotNullValidator.class)
public @interface NotBlankIfNotNull {
    String message() default "The field must not be blank";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
