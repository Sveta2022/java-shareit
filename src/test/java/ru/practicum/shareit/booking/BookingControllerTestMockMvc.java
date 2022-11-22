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


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTestMockMvc {

    @MockBean
    BookingService bookingService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    private BookingInputDto bookingInputDto1;
    private BookingDto bookingDto1;
    private Item item1;
    private User user1;
    private User user2;

    @BeforeEach
    void start() {

//        bookingDto2 = BookingDto.builder()
//                .id(2L)
//                .item(BookingDto.ItemInput.builder().id(1L).name("itemDtoName1").build())
//                .booker(BookingDto.UserInput.builder().id(2L).build())
//                .start(LocalDateTime.of(2022,9,15,14,30))
//                .end(LocalDateTime.of(2022,10,15,14,40))
//                .status(Status.APPROVED)
//                .build();

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

        bookingInputDto1 = BookingInputDto.builder()
                .id(1L)
                .itemId(item1.getId())
                .start(LocalDateTime.of(2022,6,10,14,30))
                .end(LocalDateTime.of(2022,7,10,14,40))
                .build();
        bookingDto1 = BookingDto.builder()
                .id(bookingInputDto1.getId())
                .booker(BookingDto.UserInput.builder().id(user1.getId()).build())
                .item(BookingDto.ItemInput.builder().id(item1.getId()).name(item1.getName()).build())
                .start(bookingInputDto1.getStart())
                .status(Status.APPROVED)
                .end(LocalDateTime.of(2022,7,10,14,40))
                .build();
    }

    @Test
    void create() throws Exception{
        when(bookingService.create(any(), anyLong()))
                .thenReturn(any());

        mockMvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingInputDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user1.getId()))
                .andExpect(status().isOk())
                        .andExpect(content().json(mapper.writeValueAsString(bookingDto1)));
//                .andExpect(jsonPath("$.id", is(bookingDto1.getId()), Long.class))
//                .andExpect(jsonPath("$.status", is(bookingDto1.getStatus()), Boolean.class));
//                .andExpect(jsonPath("$.booker.id", is(bookingDto1.getBooker().getId()), Long.class));

//        Mockito.verify(bookingService, Mockito.times(1))
//                .create(any(), anyLong());

    }

    @Test
    void getAllforBooker() {
    }

    @Test
    void getAllforOwner() {
    }

    @Test
    void getById() {
    }

    @Test
    void update() {
    }
}