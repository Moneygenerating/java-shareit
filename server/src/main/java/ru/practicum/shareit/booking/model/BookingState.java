package ru.practicum.shareit.booking.model;

public enum BookingState {
    //все
    ALL,
    //текущие
    CURRENT,
    //будущие
    FUTURE,
    //завершенные
    PAST,
    //Отклоненные
    REJECTED,
    //Ожидающие подтверждения
    WAITING,
    //Подтверждено
    APPROVED;

    public static BookingState from(String state) {
        for (BookingState value : BookingState.values()) {
            if (value.name().equals(state)) {
                return value;
            }
        }
        return null;
    }
}

