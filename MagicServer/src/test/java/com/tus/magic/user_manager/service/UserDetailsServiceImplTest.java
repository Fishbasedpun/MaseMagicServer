package com.tus.magic.user_manager.service;

import com.tus.magic.user_manager.models.User;
import com.tus.magic.user_manager.repo.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    private UserRepository userRepository;
    private UserDetailsServiceImpl service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        service = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    void testLoadUserByUsernameSuccess() {
        User user = new User("alice", "EncodedPass123", null);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        assertEquals(user, service.loadUserByUsername("alice"));
    }

    @Test
    void testLoadUserByUsernameThrowsWhenNotFound() {
        when(userRepository.findByUsername("bob")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("bob"));
    }
}
