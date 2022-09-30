package ru.practicum.shareit.booking.dto;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = StartBeforeEndValidator.class)
public @interface StartBeforeEnd {
    String message() default "Неправильно введена дата";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

