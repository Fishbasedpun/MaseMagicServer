package com.tus.magic.user_manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tus.magic.models.Card;
import com.tus.magic.user_manager.dto.CreateUserRequest;
import com.tus.magic.user_manager.dto.LoginRequest;
import com.tus.magic.user_manager.models.Role;
import com.tus.magic.user_manager.models.User;
import com.tus.magic.user_manager.repo.UserRepository;
import com.tus.magic.user_manager.service.UserService;
import com.tus.magic.services.JwtService;

import org.apache.hc.client5.http.auth.InvalidCredentialsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserRepository userRepository;

    private User user;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        user = new User("johnDoe", "Password123", Role.USER);
        mapper = new ObjectMapper();
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void testCreateUserSuccess() throws Exception {
        CreateUserRequest request = new CreateUserRequest("johnDoe", "Password1", Role.USER);
        when(userService.createUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("johnDoe"));
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void testCreateUserFailDueToInvalidUsername() throws Exception {
        CreateUserRequest request = new CreateUserRequest("jd", "Password1", Role.USER);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void testCreateUserFailDueToWeakPassword() throws Exception {
        CreateUserRequest request = new CreateUserRequest("johnDoe", "weak", Role.USER);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void testLoginSuccess() throws Exception {
        LoginRequest request = new LoginRequest("johnDoe", "Password1");
        when(userService.authenticate(anyString(), anyString())).thenReturn(user);
        when(jwtService.generateToken(anyString(), any(Role.class))).thenReturn("mocked-jwt");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value("mocked-jwt"));
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void testLoginFailure() throws Exception {
        LoginRequest request = new LoginRequest("johnDoe", "WrongPass");
        when(userService.authenticate(anyString(), anyString())).thenThrow(new InvalidCredentialsException("Invalid"));

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("false"));
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void testGetUserFavorites() throws Exception {
        Card card = new Card();
        card.setName("Lightning Bolt");
        user.setFavoriteCards(Set.of(card));

        when(userRepository.findByUsername("johnDoe")).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/johnDoe/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Lightning Bolt"));
    }
}
