package com.example.fastsoccer.controller;

import com.example.fastsoccer.entity.*;
import com.example.fastsoccer.repository.*;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(OwnPitchController.class)
@TestPropertySource(locations = "classpath:application-test.properties")
class OwnPitchControllerTest {

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
    void loadDistrict() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.get("/showformRegisterPitch"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.SC_OK))
                .andExpect(MockMvcResultMatchers.view().name("registerPitch"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("districtList"))
                .andExpect(MockMvcResultMatchers.model().attribute("districtList", new ArrayList<District>()))
                .andDo(print());
    }

    @Test
    void addPro() throws Exception {
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

        MockMultipartFile file1
                = new MockMultipartFile(
                "pic1",
                "hello.jpeg",
                MediaType.IMAGE_JPEG_VALUE,
                "pic1".getBytes()
        );

        MockMultipartFile file2
                = new MockMultipartFile(
                "pic2",
                "hello.jpeg",
                MediaType.IMAGE_JPEG_VALUE,
                "pic2".getBytes()
        );

        MockMultipartFile file3
                = new MockMultipartFile(
                "pic3",
                "hello.jpeg",
                MediaType.IMAGE_JPEG_VALUE,
                "pic3".getBytes()
        );


        this.mockMvc.perform(multipart("/registerPitch")
                        .file(file1).file(file2).file(file3))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.SC_OK))
                .andExpect(MockMvcResultMatchers.view().name("thankyou.html"))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin@domain.com", password = "Password", authorities = "OWN")
    void load() throws Exception {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setIdOwn(10L);
        userEntity.setUsername("test");

        Yard y = new Yard();
        y.setId(1L);
        y.setOwnPitch(new OwnPitch());
        y.setDetail("test");
        y.setName("test");

        PriceYard py = new PriceYard();
        py.setId(1L);
        py.setYardId(y);
        py.setPrice(100);

        Booking b = new Booking();
        b.setId(1L);
        b.setStatus(true);
        b.setIsReview(true);
        b.setPriceYardID(py);
        b.setUserId(userEntity);
        List<Booking> bookingList = Arrays.asList(b);


        when(bookingService.findAllByPriceYardID_YardId_OwnPitch_IdAndStatusIsTrue(anyLong())).thenReturn(bookingList);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/load-manager-own")
                        .sessionAttr("user", userEntity))
                .andExpect(MockMvcResultMatchers.view().name("ownmanager"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("bookingList"));
    }

    @Test
    @WithMockUser(username = "admin@domain.com", password = "Password", authorities = "OWN")
    void loadYardManagerOwn() throws Exception {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setIdOwn(10L);
        PriceYard py = new PriceYard();
        py.setId(1L);
        py.setYardId(new Yard());
        py.setPrice(100);
        List<PriceYard> priceYardList = Arrays.asList(py);
        Yard y = new Yard();
        y.setId(1L);
        y.setOwnPitch(new OwnPitch());
        y.setDetail("test");
        y.setPriceYardList(new HashSet<>());
        List<Yard> yardList = Arrays.asList(y);

        when(yardRepository.findAllByOwnPitch_Id(anyLong())).thenReturn(yardList);
        when(priceYardRepository.findAllByYardId_OwnPitch_Id(anyLong())).thenReturn(priceYardList);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/loadyardmanagerown")
                        .sessionAttr("user", userEntity))
                .andExpect(MockMvcResultMatchers.view().name("ownyard"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("yardList"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("priceYardList"))
                .andExpect(MockMvcResultMatchers.model().attribute("priceYardList", priceYardList));
    }

    @Test
    @WithMockUser(username = "admin@domain.com", password = "Password", authorities = "OWN")
    void loadformaddyard() throws Exception {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/loadformaddyard")
                        .sessionAttr("user", userEntity))
                .andExpect(MockMvcResultMatchers.view().name("add-yard"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"));
    }

    @Test
    @WithMockUser(username = "admin@domain.com", password = "Password", authorities = "OWN")
    void addYard() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/addyard"))
                .andExpect(MockMvcResultMatchers.view().name("redirect:/loadyardmanagerown"));
    }

    @Test
    void deleteYard() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/deleteYard").param("id", "1"))
                .andExpect(MockMvcResultMatchers.view().name("redirect:/loadyardmanagerown"));
    }

    @Test
    void loadformaddprice() throws Exception {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        Yard y = new Yard();
        y.setId(1L);
        y.setOwnPitch(new OwnPitch());
        y.setDetail("test");
        y.setPriceYardList(new HashSet<>());
        List<Yard> yardList = Arrays.asList(y);
        when(yardRepository.findAll()).thenReturn(yardList);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/loadformaddprice")
                        .sessionAttr("user", userEntity))
                .andExpect(MockMvcResultMatchers.view().name("addPriceYard"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.model().attribute("yardList", yardList));
    }

    @Test
    void addPrice() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/addprice"))
                .andExpect(MockMvcResultMatchers.view().name("redirect:/loadyardmanagerown"));
    }

    @Test
    void deletePrice() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/deletePrice").param("id", "1"))
                .andExpect(MockMvcResultMatchers.view().name("redirect:/loadyardmanagerown"));
    }
}