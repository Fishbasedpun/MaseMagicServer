package com.tus.magic.user_manager.service;

import com.tus.magic.user_manager.models.User;
import com.tus.magic.user_manager.repo.UserRepository;
import org.apache.hc.client5.http.auth.InvalidCredentialsException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void testCreateUserEncryptsPassword() {
        User user = new User("test", "plain123", null);
        when(passwordEncoder.encode("plain123")).thenReturn("encoded123");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User saved = userService.createUser(user);
        assertEquals("encoded123", saved.getPassword());
    }

    @Test
    void testAuthenticateSuccess() throws InvalidCredentialsException {
        User user = new User("test", "encoded123", null);
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("raw123", "encoded123")).thenReturn(true);

        user.setPassword("encoded123");
        User result = userService.authenticate("test", "raw123");
        assertEquals(user, result);
    }

    @Test
    void testAuthenticateFailure() {
        when(userRepository.findByUsername("test")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> userService.authenticate("test", "pass"));
    }

    @Test
    void testDeleteUserSuccess() {
        User user = new User("test", "pass", null);
        user.setId(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        boolean result = userService.deleteUser(2L);
        assertTrue(result);
        verify(userRepository).deleteById(2L);
    }

    @Test
    void testDeleteSysAdminUserFails() {
        assertThrows(IllegalStateException.class, () -> userService.deleteUser(1L));
    }
}
