package ru.practicum.shareit.booking;

import java.util.HashMap;
import java.util.Map;

public enum StatusType {
    WAITING(1),
    APPROVED(2),
    REJECTED(3),
    CANCELED(4);

    private static final Map<Integer, StatusType> statusTypes = new HashMap<>();

    static {
        for (StatusType e: values()) {
            statusTypes.put(e.code, e);
        }
    }

    private final int code;

    StatusType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static StatusType getByCode(int code) {
        return statusTypes.get(code);
    }
}
