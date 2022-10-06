package ru.practicum.shareit.booking.dto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

public class StartBeforeEndValidator implements ConstraintValidator<BookingTimeValidation, Object> {
    private String start;
    private String end;

    @Override
    public void initialize(BookingTimeValidation bookingTimeValidation) {
        start = bookingTimeValidation.start();
        end = bookingTimeValidation.end();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext ctx) {

        try {
            Class<? extends Object> clazz = object.getClass();
            LocalDateTime startDate = null;
            Method startGetter = clazz.getMethod(getAccessorMethodName(start));
            Object startGetterResult = startGetter.invoke(object, null);
            if (startGetterResult instanceof LocalDateTime) {
                startDate = (LocalDateTime) startGetterResult;
            } else {
                return false;
            }
            LocalDateTime endDate = null;
            Method endGetter = clazz.getMethod(getAccessorMethodName(end));
            Object endGetterResult = endGetter.invoke(object, null);
            if (endGetterResult == null) {
                return true;
            }
            if (endGetterResult instanceof LocalDateTime) {
                endDate = (LocalDateTime) endGetterResult;
            }
            assert endDate != null;
            return (startDate.isBefore(endDate));
        } catch (Throwable e) {
            System.err.println(e);
        }

        return false;
    }

    private String getAccessorMethodName(String property) {
        return "get" + Character.toUpperCase(property.charAt(0)) +
                property.substring(1);
    }
}
