package ru.practicum.shareit.booking.dto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, BookingDto> {


    @Override
    public void initialize(StartBeforeEnd startBeforeEnd) {

    }

    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext ctx) {
        if (!bookingDto.getStart().isBefore(bookingDto.getEnd())) {
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate(
                    "Неправильно введена дата");
            return false;
        }
        return true;
    }
}
