package ru.practicum.shareit.validation;

import ru.practicum.shareit.booking.dto.CreateBookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckDateValidator implements ConstraintValidator<StartBeforeEndDateValid, CreateBookingDto> {
    @Override
    public void initialize(StartBeforeEndDateValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(CreateBookingDto dto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = dto.getStart();
        LocalDateTime end = dto.getEnd();
        if (start == null || end == null) {
            return false;
        }
        return start.isBefore(end);
    }
}
