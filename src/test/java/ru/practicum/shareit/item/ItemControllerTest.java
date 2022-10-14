package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    MockMvc mockMvc;
    private ItemInfoDto itemInfoDtoOther;
    private Item itemOther;


    private ItemRequest itemRequestOne;
    private User userOne;
    private User userTwo;


    @BeforeEach
    void beforeEach() {
        userOne = new User(1L, "user 1", "user1@email");
        itemRequestOne = new ItemRequest(1L, "item1", userOne, LocalDateTime.now());
        userTwo = new User(2L, "user 2", "user2@email");

        itemInfoDtoOther = new ItemInfoDto(
                1L,
                "item",
                "description",
                true,
                userOne.getId(),
                new ItemInfoDto.BookingDto(4L, 5L),
                new ItemInfoDto.BookingDto(6L, 7L),
                List.of(new CommentDto(8L, "comment",
                        LocalDateTime.of(2022, 1, 1, 1, 1), "author")),
                itemRequestOne.getRequester().getId()
        );

        itemOther = new Item(
                1L,
                "item",
                "description",
                true,
                userOne,
                itemRequestOne
        );


    }

    @Test
    void getS() throws Exception {
        when(itemService.getItems(userOne.getId(), PageRequest.ofSize(10)))
                .thenReturn(List.of(itemInfoDtoOther));

        mockMvc.perform(get("/items")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemInfoDtoOther.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemInfoDtoOther.getName())))
                .andExpect(jsonPath("$[0].description", is(itemInfoDtoOther.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemInfoDtoOther.getAvailable())))
                .andExpect(jsonPath("$[0].owner", is(itemInfoDtoOther.getOwner()), Long.class))
                .andExpect(jsonPath("$[0].lastBooking.id", is(itemInfoDtoOther.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$[0].lastBooking.bookerId", is(itemInfoDtoOther.getLastBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$[0].nextBooking.id", is(itemInfoDtoOther.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$[0].nextBooking.bookerId", is(itemInfoDtoOther.getNextBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$[0].comments[0].id", is(itemInfoDtoOther.getComments().get(0).getId()),  Long.class))
                .andExpect(jsonPath("$[0].requestId", is(itemInfoDtoOther.getRequestId()), Long.class));
        //toDO
    }

    @Test
    void getByItemId() {
    }

    @Test
    void getAvailableItem() {
    }

    @Test
    void add() {
    }

    @Test
    void deleteItem() {
    }

    @Test
    void updateItem() {
    }

    @Test
    void saveComment() {
    }
}