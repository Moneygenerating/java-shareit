package ru.practicum.shareit.booking.dto;

import javax.validation.Constraint;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StartBeforeEndValidator.class)
public @interface BookingTimeValidation {
    String message() default "{end} должен быть позже, чем {start}";
    String start();
    String end();
    Class[] groups() default {};
    Class[] payload() default {};
}

