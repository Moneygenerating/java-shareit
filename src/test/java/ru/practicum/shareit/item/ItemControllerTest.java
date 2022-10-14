package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
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

    private ItemDto itemDtoOther;

    private CommentDto commentDto;
    private ItemRequest itemRequestOne;
    private User userOne;


    @BeforeEach
    void beforeEach() {
        userOne = new User(1L, "user 1", "user1@email");
        itemRequestOne = new ItemRequest(1L, "item1", userOne, LocalDateTime.now());

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

        itemDtoOther = new ItemDto(
                1L,
                "item",
                "description",
                true,
                userOne.getId(),
                itemRequestOne.getRequester().getId()
        );

        commentDto = new CommentDto(
                1L,
                "something",
                LocalDateTime.now(),
                "BorisTheBlade"
        );
    }

    @Test
    void getItemsFromUserID() throws Exception {
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
                .andExpect(jsonPath("$[0].lastBooking.id", is(itemInfoDtoOther.getLastBooking().getId()),
                        Long.class))
                .andExpect(jsonPath("$[0].lastBooking.bookerId", is(itemInfoDtoOther.getLastBooking().getBookerId()),
                        Long.class))
                .andExpect(jsonPath("$[0].nextBooking.id", is(itemInfoDtoOther.getNextBooking().getId()),
                        Long.class))
                .andExpect(jsonPath("$[0].nextBooking.bookerId", is(itemInfoDtoOther.getNextBooking().getBookerId()),
                        Long.class))
                .andExpect(jsonPath("$[0].comments[0].id", is(itemInfoDtoOther.getComments().get(0).getId()),
                        Long.class))
                .andExpect(jsonPath("$[0].requestId", is(itemInfoDtoOther.getRequestId()), Long.class));

        verify(itemService, times(1)).getItems(userOne.getId(), PageRequest.ofSize(10));
    }

    @Test
    void getByItemId() throws Exception {
        when(itemService.getItemById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemInfoDtoOther);

        mockMvc.perform(get("/items/{itemId}", itemInfoDtoOther.getId())
                        .header("X-Sharer-User-Id", itemInfoDtoOther.getOwner()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemInfoDtoOther.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemInfoDtoOther.getName())))
                .andExpect(jsonPath("$.description", is(itemInfoDtoOther.getDescription())))
                .andExpect(jsonPath("$.available", is(itemInfoDtoOther.getAvailable())))
                .andExpect(jsonPath("$.owner", is(itemInfoDtoOther.getOwner()), Long.class))
                .andExpect(jsonPath("$.lastBooking.id", is(itemInfoDtoOther.getLastBooking().getId()),
                        Long.class))
                .andExpect(jsonPath("$.lastBooking.bookerId", is(itemInfoDtoOther.getLastBooking().getBookerId()),
                        Long.class))
                .andExpect(jsonPath("$.nextBooking.id", is(itemInfoDtoOther.getNextBooking().getId()),
                        Long.class))
                .andExpect(jsonPath("$.nextBooking.bookerId", is(itemInfoDtoOther.getNextBooking().getBookerId()),
                        Long.class))
                .andExpect(jsonPath("$.comments[0].id", is(itemInfoDtoOther.getComments().get(0).getId()),
                        Long.class))
                .andExpect(jsonPath("$.requestId", is(itemInfoDtoOther.getRequestId()), Long.class));

        verify(itemService, times(1)).getItemById(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void getAvailableItem() throws Exception {
        when(itemService.getAvailableItems("item1", PageRequest.ofSize(10)))
                .thenReturn(List.of(itemDtoOther));

        mockMvc.perform(get("/items/search")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .param("text", "item1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDtoOther.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoOther.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoOther.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoOther.getAvailable())))
                .andExpect(jsonPath("$[0].ownerId", is(itemDtoOther.getOwnerId()), Long.class))
                .andExpect(jsonPath("$[0].requestId", is(itemDtoOther.getRequestId()), Long.class));

        verify(itemService, times(1)).getAvailableItems("item1",
                PageRequest.ofSize(10));
    }

    @Test
    void add() throws Exception {
        when(itemService.addNewItem(Mockito.anyLong(), Mockito.any(ItemDto.class)))
                .thenReturn(itemDtoOther);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoOther))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoOther.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoOther.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoOther.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoOther.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(itemDtoOther.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.requestId", is(itemDtoOther.getRequestId()), Long.class));

        verify(itemService, times(1)).addNewItem(Mockito.anyLong(), Mockito.any(ItemDto.class));
    }

    @Test
    void deleteItem() throws Exception {
        mockMvc.perform(delete("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(itemService, times(1)).deleteItem(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.updateItem(Mockito.anyLong(), Mockito.any(ItemDto.class), Mockito.anyLong()))
                .thenReturn(itemDtoOther);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .content(mapper.writeValueAsString(itemDtoOther))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoOther.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoOther.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoOther.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoOther.getAvailable())))
                .andExpect(jsonPath("$.ownerId", is(itemDtoOther.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.requestId", is(itemDtoOther.getRequestId()), Long.class));

        verify(itemService, times(1)).updateItem(Mockito.anyLong(), Mockito.any(ItemDto.class),
                Mockito.anyLong());
    }

    @Test
    void saveComment() throws Exception {
        when(itemService.addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(CommentDto.class)))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));

        verify(itemService, times(1)).addComment(Mockito.anyLong(), Mockito.anyLong(),
                Mockito.any(CommentDto.class));
    }

}