package com.example.fastsoccer.service;

import com.example.fastsoccer.controller.RestController;
import com.example.fastsoccer.entity.UserEntity;
import com.example.fastsoccer.repository.*;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@WebMvcTest(RestController.class)
class UserDetailsServiceImplTest {

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
    private UserDetailsService userDetailsService;

    @Test
    void loadUserByUsernameNull() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setIdOwn(10L);
        user.setUsername("userTest1");
        user.setPassword("password");
        when(userRepository.findAllByUsername(anyString())).thenReturn(null);
        try {
            userDetailsService.loadUserByUsername("userTest1");
        } catch (Exception e) {
            Assert.assertEquals("Could not find user",e.getMessage());
        }

    }

    @Test
    void loadUserByUsernameSucess() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setIdOwn(10L);
        user.setUsername("userTest1");
        user.setPassword("password");
        when(userRepository.findAllByUsername(anyString())).thenReturn(user);

        UserDetails result = userDetailsService.loadUserByUsername("userTest1");
        Assert.assertEquals(result.getUsername(),user.getUsername());
        Assert.assertEquals(result.getPassword(),user.getPassword());


    }

}