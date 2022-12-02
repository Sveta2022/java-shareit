package ru.practicum.shareit.booking;


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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTestMockMvc {

    @MockBean
    private BookingService bookingService;
    private MockMvc mockMvc;
    private ObjectMapper mapper;
    private BookingInputDto bookingInputDto1;
    private BookingInputDto bookingInputDto2;
    private BookingDto bookingDto1;
    private BookingDto bookingDto2;
    private Item item1;
    private Item item2;
    private User user1;
    private User user2;

    @Autowired
    public BookingControllerTestMockMvc(BookingService bookingService, MockMvc mockMvc, ObjectMapper mapper) {
        this.bookingService = bookingService;
        this.mockMvc = mockMvc;
        this.mapper = mapper;
    }

    @BeforeEach
    void start() {
        user1 = User.builder()
                .id(1L)
                .name("user1")
                .email("user1@mail.ru")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@mail.ru")
                .build();

        item1 = Item.builder()
                .id(1L)
                .name("itemName1")
                .description("itemDescription1")
                .owner(user2)
                .available(true)
                .build();
        item2 = Item.builder()
                .id(2L)
                .name("itemName2")
                .description("itemDescription2")
                .owner(user1)
                .available(true)
                .build();

        bookingInputDto1 = BookingInputDto.builder()
                .id(1L)
                .itemId(item1.getId())
                .start(LocalDateTime.of(2023, 6, 10, 14, 30))
                .end(LocalDateTime.of(2023, 7, 10, 14, 40))
                .build();

        bookingInputDto2 = BookingInputDto.builder()
                .id(2L)
                .itemId(item2.getId())
                .start(LocalDateTime.of(2024, 6, 10, 14, 30))
                .end(LocalDateTime.of(2024, 7, 10, 14, 40))
                .build();

        bookingDto1 = BookingDto.builder()
                .id(bookingInputDto1.getId())
                .booker(BookingDto.UserInput.builder().id(user1.getId()).build())
                .item(BookingDto.ItemInput.builder().id(item1.getId()).name(item1.getName()).build())
                .start(bookingInputDto1.getStart())
                .status(Status.APPROVED)
                .end(bookingInputDto1.getEnd())
                .build();

        bookingDto2 = BookingDto.builder()
                .id(2L)
                .item(BookingDto.ItemInput.builder().id(item2.getId()).name(item1.getName()).build())
                .booker(BookingDto.UserInput.builder().id(user2.getId()).build())
                .start(bookingInputDto2.getStart())
                .status(Status.APPROVED)
                .end(bookingInputDto2.getEnd())
                .build();
    }

    @Test
    void create() throws Exception {
        when(bookingService.create(any(), anyLong()))
                .thenReturn(bookingDto1);

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingInputDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto1)));

        Mockito.verify(bookingService, Mockito.times(1))
                .create(any(), anyLong());
    }

    @Test
    void getAllforBooker() throws Exception {
        when(bookingService.getAllByBooker(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto1));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto1))));

        Mockito.verify(bookingService, Mockito.times(1))
                .getAllByBooker(anyLong(), any(), anyInt(), anyInt());
    }

//    @Test
//    void getAllforBookerWrongState() throws Exception {
//        when(bookingService.getAllByBooker(anyLong(), any(), anyInt(), anyInt()))
//                .thenReturn(List.of(bookingDto1));
//
//        mockMvc.perform(get("/bookings?state=Unknown")
//                        .header("X-Sharer-User-Id", user1.getId()))
//                .andExpect(status().isInternalServerError());
//
//        Mockito.verify(bookingService, Mockito.never())
//                .getAllByBooker(anyLong(), any(), anyInt(), anyInt());
//    }

    @Test
    void getAllforOwner() throws Exception {
        when(bookingService.getAllByOwner(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDto1));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", user2.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto1))));

        Mockito.verify(bookingService, Mockito.times(1))
                .getAllByOwner(anyLong(), any(), anyInt(), anyInt());
    }

    @Test
    void getById() throws Exception {
        when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(bookingDto1);

        mockMvc.perform(get("/bookings/" + bookingDto1.getId())
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto1)));

        Mockito.verify(bookingService, Mockito.times(1))
                .getById(anyLong(), anyLong());
    }

    @Test
    void getByIdWrongRequest() throws Exception {
        when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(bookingDto1);

        mockMvc.perform(get("/bookin/" + bookingDto1.getId())
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpect(status().isNotFound());

        Mockito.verify(bookingService, Mockito.never())
                .getById(anyLong(), anyLong());
    }

    @Test
    void update() throws Exception {
        when(bookingService.update(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto1);

        mockMvc.perform(patch("/bookings/" + bookingDto1.getId())
                        .content(mapper.writeValueAsString(bookingDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto1)));

        Mockito.verify(bookingService, Mockito.times(1))
                .update(anyLong(), anyLong(), anyBoolean());
    }
}