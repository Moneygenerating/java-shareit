package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    MockMvc mockMvc;

    private ItemRequest itemRequestOne;

    private ItemRequestDto itemRequestDto;

    private User userOne;

    private ItemRequestDto.Item item;

    @BeforeEach
    void beforeEach() {
        userOne = new User(1L, "user 1", "user1@email");
        itemRequestOne = new ItemRequest(1L, "item1", userOne, LocalDateTime.now());
        item = new ItemRequestDto.Item(1L, "Test Item", "Item Description", true,
                userOne.getId());

        itemRequestDto = new ItemRequestDto(1L, itemRequestOne.getDescription(),
                userOne.getId(), LocalDateTime.now(), List.of(item));

    }

    @Test
    void getUserRequests() throws Exception {
        when(itemRequestService.getUserRequests(userOne.getId()))
                .thenReturn(List.of(itemRequestDto));
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userOne.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].id", is(itemRequestDto.getItems().get(0).getId()),
                        Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(itemRequestDto.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].items[0].description",
                        is(itemRequestDto.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(itemRequestDto.getItems().get(0).getAvailable())))
                .andExpect(jsonPath("$[0].items[0].requestId", is(itemRequestDto.getItems().get(0).getRequestId()),
                        Long.class));

        verify(itemRequestService, times(1)).getUserRequests(userOne.getId());
    }

    @Test
    void createItemRequest() throws Exception {
        when(itemRequestService.saveItem(Mockito.any(ItemRequestDto.class), Mockito.anyLong()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userOne.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.items[0].id", is(itemRequestDto.getItems().get(0).getId()),
                        Long.class))
                .andExpect(jsonPath("$.items[0].name", is(itemRequestDto.getItems().get(0).getName())))
                .andExpect(jsonPath("$.items[0].description",
                        is(itemRequestDto.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$.items[0].available", is(itemRequestDto.getItems().get(0).getAvailable())))
                .andExpect(jsonPath("$.items[0].requestId", is(itemRequestDto.getItems().get(0).getRequestId()),
                        Long.class));

        verify(itemRequestService, times(1)).saveItem(Mockito.any(ItemRequestDto.class),
                Mockito.anyLong());
    }

    @Test
    void getOtherAllRequests() throws Exception {

        when(itemRequestService.getOtherAllRequests(Mockito.anyLong(), Mockito.any(PageRequest.class)))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].items[0].id", is(itemRequestDto.getItems().get(0).getId()),
                        Long.class))
                .andExpect(jsonPath("$[0].items[0].name", is(itemRequestDto.getItems().get(0).getName())))
                .andExpect(jsonPath("$[0].items[0].description",
                        is(itemRequestDto.getItems().get(0).getDescription())))
                .andExpect(jsonPath("$[0].items[0].available", is(itemRequestDto.getItems().get(0).getAvailable())))
                .andExpect(jsonPath("$[0].items[0].requestId", is(itemRequestDto.getItems().get(0).getRequestId()),
                        Long.class));

        verify(itemRequestService, times(1)).getOtherAllRequests(Mockito.anyLong(),
                Mockito.any(PageRequest.class));
    }

    @Test
    void getRequestById() throws Exception {
        when(itemRequestService.getRequestById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", itemRequestDto.getId())
                        .header("X-Sharer-User-Id", userOne.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.requesterId", is(itemRequestDto.getRequesterId()), Long.class));

        verify(itemRequestService, times(1)).getRequestById(Mockito.anyLong(), Mockito.anyLong());
    }
}