package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTestMockMvc {

    @MockBean
    private ItemService itemService;
    private MockMvc mockMvc;
    private ObjectMapper mapper;
    private UserDto userDto1;
    private ItemDto itemDto1;
    private ItemDto itemDto2;
    private CommentDto commentDto1;

    @Autowired
    public ItemControllerTestMockMvc(ItemService itemService, MockMvc mockMvc, ObjectMapper mapper) {
        this.itemService = itemService;
        this.mockMvc = mockMvc;
        this.mapper = mapper;
    }

    @BeforeEach
    void start() {
        itemDto1 = ItemDto.builder()
                .id(1L)
                .name("itemDtoName1")
                .description("itemDtoDescription1")
                .comments(null)
                .lastBooking(null)
                .nextBooking(null)
                .available(true)
                .requestId(1L)
                .build();

        itemDto2 = ItemDto.builder()
                .id(2L)
                .name("itemDtoName2")
                .description("itemDtoDescription2")
                .comments(null)
                .lastBooking(null)
                .nextBooking(null)
                .available(true)
                .requestId(2L)
                .build();

        userDto1 = UserDto.builder()
                .id(1L)
                .name("user1")
                .email("user1@u@mail.ru")
                .build();

        commentDto1 = CommentDto.builder()
                .id(1L)
                .text("textCommentDto1")
                .idItem(1L)
                .authorName("user1")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void create() throws Exception {
        when(itemService.create(anyLong(), any()))
                .thenReturn(itemDto1);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto1.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription()), String.class));

        Mockito.verify(itemService, Mockito.times(1))
                .create(anyLong(), any());
    }

    @Test
    void update() throws Exception {
        itemService.create(userDto1.getId(), itemDto1);
        when(itemService.update(anyLong(), any(), anyLong()))
                .thenReturn(itemDto2);

        mockMvc.perform(patch("/items/" + itemDto1.getId())
                        .content(mapper.writeValueAsString(itemDto2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto2.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto2.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto2.getDescription()), String.class));

        Mockito.verify(itemService, Mockito.times(1))
                .update(anyLong(), any(), anyLong());
    }

    @Test
    void getAll() throws Exception {
        itemService.create(userDto1.getId(), itemDto1);
        itemService.create(userDto1.getId(), itemDto2);

        when(itemService.getAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto1, itemDto2));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userDto1.getId()))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto1.getName()), String.class))
                .andExpect(jsonPath("$[0].description", is(itemDto1.getDescription()), String.class));

        Mockito.verify(itemService, Mockito.times(1))
                .getAll(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getById() throws Exception {
        itemService.create(userDto1.getId(), itemDto1);
        when(itemService.getById(anyLong(), anyLong()))
                .thenReturn(itemDto1);

        mockMvc.perform(get("/items/" + itemDto1.getId())
                        .header("X-Sharer-User-Id", userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto1.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription()), String.class));

        Mockito.verify(itemService, Mockito.times(1))
                .getById(anyLong(), anyLong());
    }

    @Test
    void search() throws Exception {
        itemService.create(userDto1.getId(), itemDto1);
        when(itemService.search(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto1));

        mockMvc.perform(get("/items/search").param("text", "from", "size")
                        .header("X-Sharer-User-Id", userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto1.getName()), String.class))
                .andExpect(jsonPath("$[0].description", is(itemDto1.getDescription()), String.class));

        Mockito.verify(itemService, Mockito.times(1))
                .search(anyString(), anyInt(), anyInt());
    }

    @Test
    void addComment() throws Exception {
        itemService.create(userDto1.getId(), itemDto1);
        when(itemService.addComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto1);

        mockMvc.perform(post("/items/" + itemDto1.getId() + "/comment")
                        .content(mapper.writeValueAsString(commentDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto1.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto1.getText()), String.class))
                .andExpect(jsonPath("$.authorName", is(commentDto1.getAuthorName()), String.class));

        Mockito.verify(itemService, Mockito.times(1))
                .addComment(anyLong(), anyLong(), any());
    }
}