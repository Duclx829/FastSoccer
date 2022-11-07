package com.example.fastsoccer.controller;

import com.example.fastsoccer.entity.*;
import com.example.fastsoccer.repository.*;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(RestController.class)
class RestControllerTest {

    @MockBean
    private OwnPitchRepository ownPitchRepository;
    @MockBean
    private DistricRepository districRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private BookingService bookingService;
    @MockBean
    private YardRepository yardRepository;
    @MockBean
    private PriceYardRepository priceYardRepository;
    @MockBean
    private PostRepository postRepository;
    @MockBean
    private ReviewRepository reviewRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getTime() throws Exception {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setIdOwn(10L);


        Yard y = new Yard();
        y.setId(1L);
        y.setOwnPitch(new OwnPitch());
        y.setDetail("test");
        y.setPriceYardList(new HashSet<>());

        PriceYard py = new PriceYard();
        py.setId(1L);
        py.setYardId(y);
        py.setPrice(100);
        List<PriceYard> priceYardList = Arrays.asList(py);
        when(priceYardRepository.findAllPriceYardByYardID(anyLong())).thenReturn(priceYardList);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/yard/getTime/1"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.SC_OK))
                .andDo(print());
    }

    @Test
    void getPriceYardByYard() throws Exception {

        Yard y = new Yard();
        y.setId(1L);
        y.setOwnPitch(new OwnPitch());
        y.setDetail("test");
        y.setPriceYardList(new HashSet<>());

        when(yardRepository.findById(anyLong())).thenReturn(Optional.of(y));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/yard/1"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.SC_OK))
                .andDo(print());
    }

    @Test
    void getBooking() throws Exception {

        Yard y = new Yard();
        y.setId(1L);
        y.setOwnPitch(new OwnPitch());
        y.setDetail("test");
        y.setPriceYardList(new HashSet<>());

        PriceYard py = new PriceYard();
        py.setId(1L);
        py.setYardId(y);
        py.setPrice(100);
        List<PriceYard> priceYardList = Arrays.asList(py);

        when(bookingService.findAllPriceYardIsBooking( any(), anyLong())).thenReturn(Collections.singletonList(Long.valueOf("12345")));
        when(priceYardRepository.findAllYardNotReserved( any(), anyLong())).thenReturn(priceYardList);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/getBooking/2022-08-20/1"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.SC_OK))
                .andDo(print());
    }

    @Test
    void getPostMatchingByDistrict() throws Exception {

        District d = new District();
        d.setId(1L);
        d.setName("test");
        d.setType("Test");
        when(districRepository.findById(anyLong())).thenReturn(Optional.of(d));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/postMatching/1"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.SC_OK))
                .andDo(print());
    }
}