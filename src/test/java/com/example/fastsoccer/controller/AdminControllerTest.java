package com.example.fastsoccer.controller;

import com.example.fastsoccer.entity.Booking;
import com.example.fastsoccer.entity.District;
import com.example.fastsoccer.entity.OwnPitch;
import com.example.fastsoccer.entity.PriceYard;
import com.example.fastsoccer.entity.UserEntity;
import com.example.fastsoccer.repository.BookingService;
import com.example.fastsoccer.repository.OwnPitchRepository;
import com.example.fastsoccer.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(AdminController.class)
public class AdminControllerTest {

    @MockBean
    private BookingService bookingService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private OwnPitchRepository ownPitchRepository;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser(username = "admin@domain.com", password = "Password", authorities = "ADMIN")
    public void shouldReturnAdminPage_HaveSumBooking() throws Exception {
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setRole("ADMIN");
        entity.setUsername("testUserName");
        entity.setPassword("testpw");
        entity.setToken(23123123);
        entity.setIdOwn(123123L);
        // Mock role admin
        when(userRepository.findAllByUsername(anyString())).thenReturn(entity);
        //mock tổng số người dùng là 20
        when(userRepository.countUser()).thenReturn(20);
        //mock tổng số sân là 5
        when(ownPitchRepository.countOwnPitch()).thenReturn(10);
        //mock tổng số lịch đặt sân
        when(bookingService.countBooking()).thenReturn(3);
        //mock tổng doanh thu
        when(bookingService.sumAmountBooking()).thenReturn(1200000);

        // Gọi API và verify item màn hình
        this.mockMvc.perform(MockMvcRequestBuilders.get("/admin"))
                // trạng thái request là redirect về trang admin
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("admin/dashboard"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("counUser"))
                .andExpect(MockMvcResultMatchers.model().attribute("counUser", 20))
                .andExpect(MockMvcResultMatchers.model().attributeExists("counOwnPitch"))
                .andExpect(MockMvcResultMatchers.model().attribute("counOwnPitch", 10))
                .andExpect(MockMvcResultMatchers.model().attributeExists("countBooking"))
                .andExpect(MockMvcResultMatchers.model().attribute("countBooking", 3))
                .andExpect(MockMvcResultMatchers.model().attributeExists("sumAmountBooking"))
                .andExpect(MockMvcResultMatchers.model().attribute("sumAmountBooking", 1200000));
    }

    @Test
    @WithMockUser(username = "admin@domain.com", password = "Password", authorities = "ADMIN")
    public void shouldReturnAdminPage_NotHaveSumBooking() throws Exception {

        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setRole("ADMIN");
        entity.setUsername("testUserName");
        entity.setPassword("testpw");
        entity.setToken(23123123);
        entity.setIdOwn(123123L);
        // Mock role admin
        when(userRepository.findAllByUsername(anyString())).thenReturn(entity);
        //mock tổng số người dùng là 20
        when(userRepository.countUser()).thenReturn(20);
        //mock tổng số sân là 5
        when(ownPitchRepository.countOwnPitch()).thenReturn(10);
        //mock tổng số lịch đặt sân
        when(bookingService.countBooking()).thenReturn(0);

        // Gọi API và verify item màn hình
        this.mockMvc.perform(MockMvcRequestBuilders.get("/admin"))
                // trạng thái request là redirect về trang admin
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("admin/dashboard"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("counUser"))
                .andExpect(MockMvcResultMatchers.model().attribute("counUser", 20))
                .andExpect(MockMvcResultMatchers.model().attributeExists("counOwnPitch"))
                .andExpect(MockMvcResultMatchers.model().attribute("counOwnPitch", 10))
                .andExpect(MockMvcResultMatchers.model().attributeExists("countBooking"))
                .andExpect(MockMvcResultMatchers.model().attribute("countBooking", 0))
                .andExpect(MockMvcResultMatchers.model().attributeExists("sumAmountBooking"))
                .andExpect(MockMvcResultMatchers.model().attribute("sumAmountBooking", 0));
    }

    @Test
    public void shouldReturnProfileAdmin() throws Exception {
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setRole("ADMIN");
        entity.setUsername("testUserName");
        entity.setPassword("testpw");
        entity.setToken(23123123);
        entity.setIdOwn(123123L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setDateBooking(new Date(Calendar.getInstance().getTime().getTime()));
        booking.setStatus(true);
        booking.setIsReview(true);
        PriceYard priceYard = new PriceYard();
        priceYard.setId(1L);
        priceYard.setPrice(100000);
        priceYard.setStartTime("2021-12-12");
        priceYard.setEndTime("2022-12-11");
        booking.setPriceYardID(priceYard);

        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        entity.setBookingList(bookingList);

        List<UserEntity> userEntityList = new ArrayList<>();
        userEntityList.add(entity);

        // mock repository để thực hiện trả về data cho list user
        when(userRepository.findAll()).thenReturn(userEntityList);

        // Gọi API và verify item màn hình
        this.mockMvc.perform(MockMvcRequestBuilders.get("/frofileAdmin"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.SC_OK))
                .andExpect(MockMvcResultMatchers.view().name("admin/user"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("userList"))
                .andExpect(MockMvcResultMatchers.model().attribute("userList", userEntityList))
                .andDo(print());
    }

    @Test
    public void shouldReturnLoadPitchNotAllow() throws Exception {
        OwnPitch ownPitch = new OwnPitch();
        ownPitch.setId(1L);
        ownPitch.setDisable(false);
        ownPitch.setStatus(false);
        ownPitch.setNamePitch("test pitch");
        ownPitch.setAddress("Hanoi");
        ownPitch.setNamePitch("DongDo");
        ownPitch.setDistrict(new District());
        ownPitch.setPhone("123123213");
        ownPitch.setMail("test@gmail.com");

        List<OwnPitch> ownPitchList = new ArrayList<>();
        ownPitchList.add(ownPitch);

        // mock repository để thực hiện trả về data cho list user
        when(ownPitchRepository.findAll()).thenReturn(ownPitchList);

        // Gọi API và verify item màn hình
        this.mockMvc.perform(MockMvcRequestBuilders.get("/loadPitchNotAllow"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.SC_OK))
                .andExpect(MockMvcResultMatchers.view().name("admin/tables"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("ownPitchListOk"))
                .andExpect(MockMvcResultMatchers.model().attribute("ownPitchListOk", ownPitchList))
                .andDo(print());
    }

    @Test
    public void shouldReturnUpdatePage() throws Exception {
        OwnPitch ownPitch = new OwnPitch();
        ownPitch.setId(1L);
        ownPitch.setDisable(false);
        ownPitch.setStatus(false);
        ownPitch.setNamePitch("test pitch");
        ownPitch.setAddress("Hanoi");
        ownPitch.setNamePitch("DongDo");
        ownPitch.setDistrict(new District());
        ownPitch.setPhone("123123213");
        ownPitch.setMail("test@gmail.com");

        // mock repository để thực hiện trả về data cho list user
        when(ownPitchRepository.findById(anyLong())).thenReturn(Optional.of(ownPitch));

        // Gọi API và verify item màn hình
        this.mockMvc.perform(MockMvcRequestBuilders.get("/update").param("id", "1"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.SC_OK))
                .andExpect(MockMvcResultMatchers.view().name("admin/detail"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("ownPitch"))
                .andExpect(MockMvcResultMatchers.model().attribute("ownPitch", ownPitch))
                .andDo(print());
    }

    @Test
    public void shouldReturnUpdateStatus() throws Exception {
        OwnPitch ownPitch = new OwnPitch();
        ownPitch.setId(1L);
        ownPitch.setDisable(false);
        ownPitch.setStatus(false);
        ownPitch.setNamePitch("test pitch");
        ownPitch.setAddress("Hanoi");
        ownPitch.setNamePitch("DongDo");
        ownPitch.setDistrict(new District());
        ownPitch.setPhone("123123213");
        ownPitch.setMail("test@gmail.com");

        // Gọi API và verify item màn hình
        this.mockMvc.perform(MockMvcRequestBuilders.post("/updateStatus").flashAttr("obj", ownPitch))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/loadPitchNotAllow"))
                .andDo(print());
    }

    @Test
    public void shouldReturnCreateAccount() throws Exception {
        OwnPitch ownPitch = new OwnPitch();
        ownPitch.setId(1L);
        ownPitch.setDisable(false);
        ownPitch.setStatus(false);
        ownPitch.setNamePitch("test pitch");
        ownPitch.setAddress("Hanoi");
        ownPitch.setNamePitch("DongDo");
        ownPitch.setDistrict(new District());
        ownPitch.setPhone("123123213");
        ownPitch.setMail("test@gmail.com");

        // mock repository để thực hiện trả về data cho list user
        when(ownPitchRepository.findById(anyLong())).thenReturn(Optional.of(ownPitch));

        // Gọi API và verify item màn hình
        this.mockMvc.perform(MockMvcRequestBuilders.get("/createacount").param("id", "1"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.SC_OK))
                .andExpect(MockMvcResultMatchers.view().name("admin/createAccountOwn"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("ownPitch"))
                .andExpect(MockMvcResultMatchers.model().attribute("ownPitch", ownPitch))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", new UserEntity()))
                .andDo(print());
    }

    @Test
    @Ignore
    public void shouldReturnCreateAccountOwn() throws Exception {
        OwnPitch ownPitch = new OwnPitch();
        ownPitch.setId(1L);
        ownPitch.setDisable(false);
        ownPitch.setStatus(false);
        ownPitch.setNamePitch("test pitch");
        ownPitch.setAddress("Hanoi");
        ownPitch.setNamePitch("DongDo");
        ownPitch.setDistrict(new District());
        ownPitch.setPhone("123123213");
        ownPitch.setMail("test@gmail.com");

        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setRole("ADMIN");
        entity.setUsername("testUserName");
        entity.setPassword("testpw");
        entity.setToken(23123123);
        entity.setIdOwn(123123L);

        // mock repository để thực hiện trả về data cho list user
        when(ownPitchRepository.findById(anyLong())).thenReturn(Optional.of(ownPitch));

        // Gọi API và verify item màn hình
        this.mockMvc.perform(MockMvcRequestBuilders.post("/createAccountOwn")
                        .flashAttr("user", entity))
                .andExpect(MockMvcResultMatchers.redirectedUrl("/loadPitchNotAllow"))
                .andDo(print());
    }

    @Test
    public void shouldReturnDisablePitch() throws Exception {
        OwnPitch ownPitch = new OwnPitch();
        ownPitch.setId(1L);
        ownPitch.setDisable(false);
        ownPitch.setStatus(false);
        ownPitch.setNamePitch("test pitch");
        ownPitch.setAddress("Hanoi");
        ownPitch.setNamePitch("DongDo");
        ownPitch.setDistrict(new District());
        ownPitch.setPhone("123123213");
        ownPitch.setMail("test@gmail.com");

        // mock repository để thực hiện trả về data cho list user
        when(ownPitchRepository.findById(anyLong())).thenReturn(Optional.of(ownPitch));

        // Gọi API và verify item màn hình
        this.mockMvc.perform(MockMvcRequestBuilders.get("/disablePitch").param("id", "1"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/loadPitchNotAllow"))
                .andDo(print());
    }

    @Test
    public void shouldReturnUnDisablePitch() throws Exception {
        OwnPitch ownPitch = new OwnPitch();
        ownPitch.setId(1L);
        ownPitch.setDisable(false);
        ownPitch.setStatus(false);
        ownPitch.setNamePitch("test pitch");
        ownPitch.setAddress("Hanoi");
        ownPitch.setNamePitch("DongDo");
        ownPitch.setDistrict(new District());
        ownPitch.setPhone("123123213");
        ownPitch.setMail("test@gmail.com");

        // mock repository để thực hiện trả về data cho list user
        when(ownPitchRepository.findById(anyLong())).thenReturn(Optional.of(ownPitch));

        // Gọi API và verify item màn hình
        this.mockMvc.perform(MockMvcRequestBuilders.get("/undisablePitch").param("id", "1"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/loadPitchNotAllow"))
                .andDo(print());
    }

    @Test
    public void shouldReturnDeletePitch() throws Exception {

        // Gọi API và verify item màn hình
        this.mockMvc.perform(MockMvcRequestBuilders.get("/deletePitch").param("id", "1"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/loadPitchNotAllow"))
                .andDo(print());
    }
}