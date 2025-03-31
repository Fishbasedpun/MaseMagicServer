package com.tus.magic.user_manager.controller;

import com.tus.magic.models.Card;
import com.tus.magic.user_manager.models.User;
import com.tus.magic.user_manager.repo.UserRepository;
import com.tus.magic.user_manager.service.FavoriteService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FavoriteService favoriteService;

    @MockBean
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testUser", "Password123", null);
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void testAddFavoriteSuccess() throws Exception {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(favoriteService.addFavoriteCard("testUser", 1L)).thenReturn(true);

        mockMvc.perform(post("/favorites/add")
                .param("username", "testUser")
                .param("cardId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Card added to favorites."));
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void testAddFavoriteFailure() throws Exception {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(favoriteService.addFavoriteCard("testUser", 1L)).thenReturn(false);

        mockMvc.perform(post("/favorites/add")
                .param("username", "testUser")
                .param("cardId", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("User or Card not found."));
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void testRemoveFavoriteSuccess() throws Exception {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(favoriteService.removeFavoriteCard("testUser", 1L)).thenReturn(true);

        mockMvc.perform(delete("/favorites/remove")
                .param("username", "testUser")
                .param("cardId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Card removed from favorites."));
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void testStatsSuccess() throws Exception {
        Card card = new Card();
        card.setName("SampleCard");
        user.addFavoriteCard(card);

        when(userRepository.findAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/favorites/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.SampleCard").value(1));
    }
}
