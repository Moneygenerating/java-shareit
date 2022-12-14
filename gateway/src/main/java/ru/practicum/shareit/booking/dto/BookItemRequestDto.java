package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.service.Create;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@BookingTimeValidation(start = "start", end = "end", groups = {Create.class})
public class BookItemRequestDto {
    private long itemId;
    @FutureOrPresent(groups = {Create.class})
    private LocalDateTime start;
    @Future(groups = {Create.class})
    private LocalDateTime end;
}
