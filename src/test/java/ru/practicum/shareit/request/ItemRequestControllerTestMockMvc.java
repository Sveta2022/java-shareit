package ru.practicum.shareit.request;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTestMockMvc {

    @MockBean
    RequestService requestService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    ItemRequestDto itemRequestDto1;
    private User user1;

    @BeforeEach
    void start() {
        user1 = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();

        itemRequestDto1 = ItemRequestDto.builder()
                .id(1L)
                .description("itemRequest1Description")
                .items(null)
                .created(LocalDateTime.now().plusDays(15))
                .build();
    }

    @Test
    void create() throws Exception {

        when(requestService.create(anyLong(), any()))
                .thenReturn(itemRequestDto1);

        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemRequestDto1)));

        Mockito.verify(requestService, Mockito.times(1))
                .create(anyLong(), any());

    }

    @Test
    void getAllOwnRequests() throws Exception {

        when(requestService.getAll(anyLong()))
                .thenReturn(List.of(itemRequestDto1));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestDto1))));

        Mockito.verify(requestService, Mockito.times(1))
                .getAll(anyLong());
    }

    @Test
    void getById() throws Exception {
        when(requestService.getById(anyLong(), anyLong()))
                .thenReturn(itemRequestDto1);

        mockMvc.perform(get("/requests/" + itemRequestDto1.getId())
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemRequestDto1)));

        Mockito.verify(requestService, Mockito.times(1))
                .getById(anyLong(), anyLong());
    }

    @Test
    void getAllByPage() throws Exception {
        when(requestService.getAllByPage(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto1));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestDto1))));

        Mockito.verify(requestService, Mockito.times(1))
                .getAllByPage(anyLong(), anyInt(), anyInt());
    }
}