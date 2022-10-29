package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemInfoDtoTest {
    @Autowired
    private JacksonTester<ItemInfoDto> json;

    @Test
    void testItemDto() throws IOException {
        ItemInfoDto itemDto = new ItemInfoDto(
                1L,
                "item",
                "description",
                true,
                3L,
                new ItemInfoDto.BookingDto(4L, 5L),
                new ItemInfoDto.BookingDto(6L, 7L),
                List.of(new CommentDto(8L, "comment",
                        LocalDateTime.of(2022, 1, 1, 1, 1), "author")),
                2L
        );

        JsonContent<ItemInfoDto> result = json.write(itemDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemDto.getId().intValue());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.getAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.owner").isEqualTo(itemDto.getOwner().intValue());
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(4);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(5);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(6);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(7);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(8);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("comment");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].created").isEqualTo("2022-01-01T01:01:00");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("author");
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(itemDto.getRequestId().intValue());


    }
}