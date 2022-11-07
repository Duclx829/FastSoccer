package com.example.fastsoccer.repository;

import com.example.fastsoccer.entity.UserEntity;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MyUserDetailsTest {

    @Autowired
    private UserDetails userDetails;

    @Test
    void getAuthorities() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setIdOwn(10L);
        user.setUsername("userTest1");
        user.setPassword("password");
        user.setRole("ADMIN");
        userDetails = new MyUserDetails(user);
        List<SimpleGrantedAuthority> result = (List<SimpleGrantedAuthority>) userDetails.getAuthorities();
        Assert.assertEquals(user.getRole(), result.get(0).getAuthority());
    }

    @Test
    void getPassword() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setIdOwn(10L);
        user.setUsername("userTest1");
        user.setPassword("password");
        user.setRole("ADMIN");
        userDetails = new MyUserDetails(user);

        Assert.assertEquals(user.getPassword(), userDetails.getPassword());
    }

    @Test
    void getUsername() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setIdOwn(10L);
        user.setUsername("userTest1");
        user.setPassword("password");
        user.setRole("ADMIN");
        userDetails = new MyUserDetails(user);

        Assert.assertEquals(user.getUsername(), userDetails.getUsername());
    }

    @Test
    void isAccountNonExpired() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setIdOwn(10L);
        user.setUsername("userTest1");
        user.setPassword("password");
        user.setRole("ADMIN");
        userDetails = new MyUserDetails(user);
        userDetails.isAccountNonExpired();
    }

    @Test
    void isAccountNonLocked() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setIdOwn(10L);
        user.setUsername("userTest1");
        user.setPassword("password");
        user.setRole("ADMIN");
        userDetails = new MyUserDetails(user);
        userDetails.isAccountNonLocked();
    }

    @Test
    void isCredentialsNonExpired() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setIdOwn(10L);
        user.setUsername("userTest1");
        user.setPassword("password");
        user.setRole("ADMIN");
        userDetails = new MyUserDetails(user);
        userDetails.isCredentialsNonExpired();
    }

    @Test
    void isEnabled() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setIdOwn(10L);
        user.setUsername("userTest1");
        user.setPassword("password");
        user.setRole("ADMIN");
        userDetails = new MyUserDetails(user);
        userDetails.isEnabled();
    }
}