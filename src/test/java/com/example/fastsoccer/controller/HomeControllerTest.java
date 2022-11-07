package com.example.fastsoccer.controller;

import com.example.fastsoccer.entity.*;
import com.example.fastsoccer.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.http.NetworkHttpClient;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import mockit.Mocked;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.sql.Date;
import java.util.*;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(HomeController.class)
class HomeControllerTest {

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

    @MockBean
    private Message message;

    @Mocked
    private NetworkHttpClient networkHttpClient;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void loadPage() throws Exception {

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
        when(ownPitchRepository.findOwnPitchSuccess()).thenReturn(ownPitchList);

        // Gọi API và verify item màn hình
        this.mockMvc.perform(MockMvcRequestBuilders.get("/loadPage"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("index"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("ownPitchListOk"))
                .andExpect(MockMvcResultMatchers.model().attribute("ownPitchListOk", ownPitchList))
                .andDo(print());
    }

    @Test
    void testLoadPage() {
    }

    @Test
    void loadPageAfterLogin() throws Exception {

        // Gọi API và verify item màn hình
        this.mockMvc.perform(MockMvcRequestBuilders.get("/loadPageAfterLogin"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/loadPage"))
                .andDo(print());
    }

    @Test
    void loadFormLogin() throws Exception {
        // Gọi API và verify item màn hình
        this.mockMvc.perform(MockMvcRequestBuilders.get("/loadFormLogin"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("loginform"))
                .andDo(print());
    }

    @Test
    void showRegistrationForm() throws Exception {
        // Gọi API và verify item màn hình
        this.mockMvc.perform(MockMvcRequestBuilders.get("/loadFormRegister"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.SC_OK))
                .andExpect(MockMvcResultMatchers.view().name("register"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"))
                .andExpect(MockMvcResultMatchers.model().attribute("user", new UserEntity()))
                .andDo(print());
    }

    @Test
    void processRegisterUserExist() throws Exception {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setIdOwn(10L);
        user.setUsername("userTest1");
        user.setPassword("password");

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

        List<String> listUsername = Arrays.asList("userTest1");
        // mock repository để thực hiện trả về data cho list user
        when(userRepository.getListUsername()).thenReturn(listUsername);

        // Gọi API và verify item màn hình
        this.mockMvc.perform(MockMvcRequestBuilders.post("/process_register")
                        .sessionAttr("user", user)
                        .flashAttr("user",user))
                .andExpect(MockMvcResultMatchers.view().name("redirect:/loadFormRegister"))
                .andExpect(MockMvcResultMatchers.request().sessionAttribute("userExit", "SDT đã tồn tại"));
    }

    @Test
    void processRegister() throws Exception {

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setIdOwn(10L);
        user.setUsername("userTest1");
        user.setPassword("password");

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

        List<String> listUsername = Arrays.asList("userTest1");
        // mock repository để thực hiện trả về data cho list user
        when(userRepository.getListUsername()).thenReturn(listUsername);

        // Gọi API và verify item màn hình
        //this.mockMvc.perform(MockMvcRequestBuilders.post("/process_register")
        //                .sessionAttr("user", user)
        //                .flashAttr("user",user))
        //        .andExpect(MockMvcResultMatchers.view().name("redirect:/loadFormRegister"))
        //        .andExpect(MockMvcResultMatchers.request().sessionAttribute("userExit", "SDT đã tồn tại"));
    }

    @Test
    void logoutPage() throws Exception {

        // Gọi API và verify item màn hình
        this.mockMvc.perform(MockMvcRequestBuilders.get("/logout-success"))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/loadPage"))
                .andDo(print());
    }

    @Test
    void showDetail() throws Exception {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setIdOwn(10L);

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


        Yard yard = new Yard();
        yard.setId(1L);
        yard.setName("test name");
        yard.setDetail("Detail Info");
        yard.setOwnPitch(ownPitch);

        Set<PriceYard> priceYardSet = new HashSet<>();
        priceYardSet.add(new PriceYard());
        yard.setPriceYardList(priceYardSet);

        List<Yard> yardList = Arrays.asList(yard);

        PriceYard py = new PriceYard();
        py.setId(1L);
        py.setYardId(yard);
        py.setPrice(100);
        List<PriceYard> priceYardList = Arrays.asList(py);

        Review r = new Review();
        r.setId(1L);
        r.setOwnPitch(ownPitch);
        r.setUser(userEntity);
        r.setContent("test");
        List<Review> reviewList = Arrays.asList(r);
        // mock repository để thực hiện trả về data cho list user
        when(ownPitchRepository.findById(anyLong())).thenReturn(Optional.of(ownPitch));
        when(yardRepository.findAllByOwnPitch_Id(anyLong())).thenReturn(yardList);
        when(priceYardRepository.findAllByYardId_OwnPitch_Id(anyLong())).thenReturn(priceYardList);
        when(reviewRepository.findAllByOwnPitch_Id(anyLong())).thenReturn(reviewList);

        // Gọi API và verify item màn hình
        this.mockMvc.perform(MockMvcRequestBuilders.get("/showDetail").param("id", "1"))
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.SC_OK))
                .andExpect(MockMvcResultMatchers.view().name("pitchDetail1"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("ownPitch"))
                .andExpect(MockMvcResultMatchers.model().attribute("ownPitch", ownPitch))
                .andExpect(MockMvcResultMatchers.model().attributeExists("yardList"))
                .andExpect(MockMvcResultMatchers.model().attribute("yardList", yardList))
                .andExpect(MockMvcResultMatchers.model().attributeExists("priceYardList"))
                .andExpect(MockMvcResultMatchers.model().attribute("priceYardList", priceYardList))
                .andExpect(MockMvcResultMatchers.model().attributeExists("reviewList"))
                .andExpect(MockMvcResultMatchers.model().attribute("reviewList", reviewList))
                .andDo(print());
    }

    @Test
    void loadbyyard() throws Exception {

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
        ownPitch.setImg1("abc.jpg");
        ownPitch.setImg2("abc.jpg");
        ownPitch.setImg3("abc.jpg");

        Yard yard = new Yard();
        yard.setId(1L);
        yard.setName("test name");
        yard.setDetail("Detail Info");
        yard.setOwnPitch(ownPitch);
        Set<PriceYard> priceYardSet = new HashSet<>();
        priceYardSet.add(new PriceYard());
        yard.setPriceYardList(priceYardSet);

        PriceYard py = new PriceYard();
        py.setId(1L);
        py.setYardId(yard);
        py.setPrice(100);
        List<PriceYard> priceYardList = Arrays.asList(py);

        when(priceYardRepository.findAllByYardId(anyLong())).thenReturn(priceYardList);
        when(yardRepository.findById(anyLong())).thenReturn(Optional.of(yard));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/loadbyyard").param("id", "1"))
                .andExpect(MockMvcResultMatchers.view().name("pitchDetail1"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("priceYardList"))
                .andExpect(MockMvcResultMatchers.model().attribute("priceYardList", priceYardList))
                .andExpect(MockMvcResultMatchers.model().attributeExists("ownPitch"))
                .andExpect(MockMvcResultMatchers.model().attribute("ownPitch", ownPitch));

    }

    @Test
    @WithMockUser(username = "admin@domain.com", password = "Password", authorities = "USER")
    void bookingssss() throws Exception {
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

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setIdOwn(10L);
        userEntity.setUsername("test");
        booking.setUserId(userEntity);

        when(priceYardRepository.findById(anyLong())).thenReturn(Optional.of(priceYard));
        when(bookingService.save(any())).thenReturn(booking);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/booking").flashAttr("obj",booking)
                        .sessionAttr("user", userEntity))
                .andExpect(MockMvcResultMatchers.view().name(containsString("redirect:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?vnp_Amount=10000000&vnp_BankCode=NCB&vnp_Bill_Address=Test+thui&vnp_Bill_City=Ha+Noi&vnp_Bill_Country=Viet+Nam&vnp_Bill_Email=maiminhhieu1999%40gmail.com&vnp_Bill_FirstName=Quang&vnp_Bill_LastName=dz&vnp_Bill_Mobile=09123891&vnp_Command=pay&vnp_CreateDate")))
                .andDo(print());
    }

    @Test
    void myBookingNull() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/myBooking"))
                .andExpect(MockMvcResultMatchers.view().name("redirect:/loadFormLogin"));
    }

    @Test
    void myBooking() throws Exception {
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
        ownPitch.setImg1("abc.jpg");
        ownPitch.setImg2("abc.jpg");
        ownPitch.setImg3("abc.jpg");

        Yard yard = new Yard();
        yard.setId(1L);
        yard.setName("test name");
        yard.setDetail("Detail Info");
        yard.setOwnPitch(ownPitch);



        PriceYard priceYard = new PriceYard();
        priceYard.setId(1L);
        priceYard.setPrice(100000);
        priceYard.setStartTime("2021-12-12");
        priceYard.setEndTime("2022-12-11");
        priceYard.setYardId(yard);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setDateBooking(new Date(Calendar.getInstance().getTime().getTime()));
        booking.setStatus(true);
        booking.setIsReview(true);
        booking.setPriceYardID(priceYard);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setIdOwn(10L);
        userEntity.setUsername("test");
        booking.setUserId(userEntity);
        List<Booking> bookingList = Arrays.asList(booking);
        when(bookingService.findAllByUserId1(any())).thenReturn(bookingList);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/myBooking")
                        .sessionAttr("user", userEntity))
                .andExpect(MockMvcResultMatchers.view().name("myBooking"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("bookingList"))
                .andExpect(MockMvcResultMatchers.model().attribute("bookingList", bookingList));
    }

    @Test
    void payvn() throws Exception {
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
        ownPitch.setImg1("abc.jpg");
        ownPitch.setImg2("abc.jpg");
        ownPitch.setImg3("abc.jpg");

        Yard yard = new Yard();
        yard.setId(1L);
        yard.setName("test name");
        yard.setDetail("Detail Info");
        yard.setOwnPitch(ownPitch);



        PriceYard priceYard = new PriceYard();
        priceYard.setId(1L);
        priceYard.setPrice(100000);
        priceYard.setStartTime("2021-12-12");
        priceYard.setEndTime("2022-12-11");
        priceYard.setYardId(yard);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setDateBooking(new Date(Calendar.getInstance().getTime().getTime()));
        booking.setStatus(true);
        booking.setIsReview(true);
        booking.setPriceYardID(priceYard);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setIdOwn(10L);
        userEntity.setUsername("0912345678");
        booking.setUserId(userEntity);

        String body = "Fast soccer thông tin đặt sân: " + booking.getPriceYardID().getYardId().getName() + " thời gian: " + booking.getPriceYardID().getStartTime() + "-" + booking.getPriceYardID().getEndTime();
        when(bookingService.findById(anyLong())).thenReturn(Optional.of(booking));

        //when(message.creator(new PhoneNumber("+84912345678"),
        //        new PhoneNumber("+19807371127"),
        //        body).create()).thenReturn(null);
        //this.mockMvc.perform(MockMvcRequestBuilders.get("/user/pay").param("id", "1")
        //                .sessionAttr("user", userEntity))
        //        .andExpect(MockMvcResultMatchers.view().name("bookingsuccess"))
        //        .andExpect(MockMvcResultMatchers.model().attributeExists("booking"))
        //        .andExpect(MockMvcResultMatchers.model().attribute("booking", booking));
    }

    @Test
    void loadPost() throws Exception {
        Post p = new Post();
        p.setId(1L);
        p.setContent("test");
        List<Post> postList = Arrays.asList(p);
        when(postRepository.findAll()).thenReturn(postList);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/loadPost"))
                .andExpect(MockMvcResultMatchers.view().name("post"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("postList"))
                .andExpect(MockMvcResultMatchers.model().attribute("postList", postList));
    }

    @Test
    void postMatching() throws Exception {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setIdOwn(10L);
        userEntity.setUsername("0912345678");

        this.mockMvc.perform(MockMvcRequestBuilders.post("/postMatching")
                        .sessionAttr("user", userEntity))
                .andExpect(MockMvcResultMatchers.view().name("redirect:/loadMatching"));
    }

    @Test
    @WithMockUser(username = "admin@domain.com", password = "Password", authorities = "USER")
    void loadMatching() throws Exception {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setIdOwn(10L);
        userEntity.setUsername("0912345678");

        District d = new District();
        d.setId(1L);
        d.setName("test");
        d.setType("test");
        List<District> districtList = Arrays.asList(d);

        Post p = new Post();
        p.setId(1L);
        p.setContent("test");
        p.setUserEntity(userEntity);
        p.setDistrictEntity(d);
        List<Post> postList = Arrays.asList(p);

        when(districRepository.findAll()).thenReturn(districtList);
        when(postRepository.findAll((Sort) any())).thenReturn(postList);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/loadMatching"))
                .andExpect(MockMvcResultMatchers.view().name("matching"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("districtList"))
                .andExpect(MockMvcResultMatchers.model().attribute("districtList", districtList))
                .andExpect(MockMvcResultMatchers.model().attributeExists("postList"))
                .andExpect(MockMvcResultMatchers.model().attribute("postList", postList));
    }

    @Test
    void loadMatchByAdd() throws Exception {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setIdOwn(10L);
        userEntity.setUsername("0912345678");

        District d = new District();
        d.setId(1L);
        d.setName("test");
        d.setType("test");
        List<District> districtList = Arrays.asList(d);

        Post p = new Post();
        p.setId(1L);
        p.setContent("test");
        p.setUserEntity(userEntity);
        p.setDistrictEntity(d);
        List<Post> postList = Arrays.asList(p);
        when(postRepository.findAllByDistrictEntity_Id(anyLong())).thenReturn(postList);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/loadMatchByAdd").param("id","1"))
                .andExpect(MockMvcResultMatchers.view().name("matching"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("postList"))
                .andExpect(MockMvcResultMatchers.model().attribute("postList", postList));
    }

    @Test
    void deletePost() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/deletePost").param("id","1"))
                .andExpect(MockMvcResultMatchers.view().name("redirect:/loadUserProfile"));
    }

    @Test
    void loadUserProfileNull() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/loadUserProfile"))
                .andExpect(MockMvcResultMatchers.view().name("redirect:/loadFormLogin"));
    }
    @Test
    void loadUserProfile() throws Exception {
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
        ownPitch.setImg1("abc.jpg");
        ownPitch.setImg2("abc.jpg");
        ownPitch.setImg3("abc.jpg");

        Yard yard = new Yard();
        yard.setId(1L);
        yard.setName("test name");
        yard.setDetail("Detail Info");
        yard.setOwnPitch(ownPitch);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setIdOwn(10L);
        userEntity.setUsername("0912345678");

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
        priceYard.setYardId(yard);
        booking.setPriceYardID(priceYard);
        booking.setUserId(userEntity);

        District d = new District();
        d.setId(1L);
        d.setName("test");
        d.setType("test");

        Post p = new Post();
        p.setId(1L);
        p.setContent("test");
        p.setUserEntity(userEntity);
        p.setDistrictEntity(d);
        List<Post> postList = Arrays.asList(p);
        List<Booking> bookingList = Arrays.asList(booking);

        when(bookingService.findAllByUserId1(any())).thenReturn(bookingList);
        when(postRepository.findAllByUserEntity(any())).thenReturn(postList);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/loadUserProfile")
                        .sessionAttr("user", userEntity))
                .andExpect(MockMvcResultMatchers.view().name("userProfile"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("bookingList"))
                .andExpect(MockMvcResultMatchers.model().attribute("bookingList", bookingList))
                .andExpect(MockMvcResultMatchers.model().attributeExists("postList"))
                .andExpect(MockMvcResultMatchers.model().attribute("postList", postList));
    }
    @Test
    void sendOTPChangePassword() {
    }

    @Test
    void sendOTPChangePasswordNull() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/sendOTPChangePassword"))
                .andExpect(MockMvcResultMatchers.view().name("redirect:/loadFormLogin"));
    }

    @Test
    void changePasswordWrongOTP() throws Exception {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setIdOwn(10L);
        userEntity.setUsername("0912345678");
        userEntity.setToken(1155);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/changePassword")
                        .param("password","pass")
                        .param("otp","1134")
                        .sessionAttr("user", userEntity))
                .andExpect(MockMvcResultMatchers.view().name("changePassword"))
                .andExpect(MockMvcResultMatchers.request().sessionAttribute("OTPchangePassword", "sai OTP"));
    }

    @Test
    void changePassword() throws Exception {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setIdOwn(10L);
        userEntity.setUsername("0912345678");
        userEntity.setToken(1155);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/changePassword")
                        .param("password","pass")
                        .param("otp","1155")
                        .sessionAttr("user", userEntity))
                .andExpect(MockMvcResultMatchers.view().name("redirect:/loadUserProfile"));
    }

    @Test
    void loadFormforgotPassword() throws  Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/loadFormforgotPassword"))
                .andExpect(MockMvcResultMatchers.view().name("forgotPassword"));
    }

    @Test
    void forgotPasswordNull() throws  Exception {
        when(userRepository.findAllByUsername(anyString())).thenReturn(null);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/forgotPassword").param("username","0912345678"))
                .andExpect(MockMvcResultMatchers.view().name("redirect:/loadFormforgotPassword"));
    }

    @Test
    void loadFormReview() throws  Exception {
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
        ownPitch.setImg1("abc.jpg");
        ownPitch.setImg2("abc.jpg");
        ownPitch.setImg3("abc.jpg");

        Yard yard = new Yard();
        yard.setId(1L);
        yard.setName("test name");
        yard.setDetail("Detail Info");
        yard.setOwnPitch(ownPitch);

        List<Yard> yardList = Arrays.asList(yard);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setIdOwn(10L);
        userEntity.setUsername("0912345678");

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
        priceYard.setYardId(yard);
        booking.setPriceYardID(priceYard);
        booking.setUserId(userEntity);

        District d = new District();
        d.setId(1L);
        d.setName("test");
        d.setType("test");

        Post p = new Post();
        p.setId(1L);
        p.setContent("test");
        p.setUserEntity(userEntity);
        p.setDistrictEntity(d);
        List<PriceYard> priceYardList = Arrays.asList(priceYard);

        when(bookingService.findById(anyLong())).thenReturn(Optional.of(booking));
        when(ownPitchRepository.findById(anyLong())).thenReturn(Optional.of(ownPitch));
        when(yardRepository.findAllByOwnPitch_Id(anyLong())).thenReturn(yardList);
        when(priceYardRepository.findAllByYardId_OwnPitch_Id(anyLong())).thenReturn(priceYardList);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/showFormReview").param("id","1"))
                .andExpect(MockMvcResultMatchers.view().name("reviewBooking"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("booking"))
                .andExpect(MockMvcResultMatchers.model().attribute("booking", booking))
                .andExpect(MockMvcResultMatchers.model().attributeExists("ownPitch"))
                .andExpect(MockMvcResultMatchers.model().attribute("ownPitch", ownPitch))
                .andExpect(MockMvcResultMatchers.model().attributeExists("yardList"))
                .andExpect(MockMvcResultMatchers.model().attribute("yardList", yardList))
                .andExpect(MockMvcResultMatchers.model().attributeExists("priceYardList"))
                .andExpect(MockMvcResultMatchers.model().attribute("priceYardList", priceYardList));

    }

    @Test
    void postReview() throws  Exception {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setIdOwn(10L);
        userEntity.setUsername("0912345678");

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
        ownPitch.setImg1("abc.jpg");
        ownPitch.setImg2("abc.jpg");
        ownPitch.setImg3("abc.jpg");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setDateBooking(new Date(Calendar.getInstance().getTime().getTime()));
        booking.setStatus(true);
        booking.setIsReview(true);


        when(ownPitchRepository.findById(anyLong())).thenReturn(Optional.of(ownPitch));
        when(bookingService.findById(anyLong())).thenReturn(Optional.of(booking));

        this.mockMvc.perform(MockMvcRequestBuilders.post("/postReview").param("idOwnPitch","1").param("idBooking","111"))
                .andExpect(MockMvcResultMatchers.view().name("redirect:/loadUserProfile"));

    }

    @Test
    void loadFind() throws  Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/loadFind"))
                .andExpect(MockMvcResultMatchers.view().name("findPitch"));
    }

    @Test
    void loadFindPitchEmpty() throws  Exception {
        when(ownPitchRepository.search(anyString())).thenReturn(null);
        this.mockMvc.perform(MockMvcRequestBuilders.get("/loadFind").param("textSearch","txt"))
                .andExpect(MockMvcResultMatchers.view().name("findPitch"));
    }

    @Test
    void loadFindPitch() throws  Exception {
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
        ownPitch.setImg1("abc1.jpg");
        ownPitch.setImg2("abc2.jpg");
        ownPitch.setImg3("abc3.jpg");

        when(ownPitchRepository.search(anyString())).thenReturn(Arrays.asList(ownPitch));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/loadFind").param("textSearch","txt"))
                .andExpect(MockMvcResultMatchers.view().name("findPitch"));
    }
}