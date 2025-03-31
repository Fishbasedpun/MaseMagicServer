package com.tus.magic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tus.magic.models.Card;
import com.tus.magic.repository.CardRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardRepository cardRepository;

    private ObjectMapper mapper;
    private Card card;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        card = new Card("Fireball", "Spell", "Deals damage", "Red", 3, 0, 0, null);
        card.setId(1L);
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void testCreateCardWithImage() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "card.jpg", MediaType.IMAGE_JPEG_VALUE, "dummy".getBytes());

        when(cardRepository.save(any(Card.class))).thenReturn(card);

        mockMvc.perform(multipart("/api/cards")
                        .file(image)
                        .param("name", "Fireball")
                        .param("typeLine", "Spell")
                        .param("text", "Deals damage")
                        .param("color", "Red")
                        .param("cost", "3")
                        .param("power", "0")
                        .param("toughness", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Fireball"));
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void testGetAllCards() throws Exception {
        when(cardRepository.findAll()).thenReturn(List.of(card));

        mockMvc.perform(get("/api/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Fireball"));
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void testSearchCardsWithQuery() throws Exception {
        when(cardRepository.searchCards("Fire")).thenReturn(List.of(card));

        mockMvc.perform(get("/api/cards/search").param("query", "Fire"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Fireball"));
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void testSearchCardsWithoutQuery() throws Exception {
        mockMvc.perform(get("/api/cards/search"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Search query cannot be empty."));
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void testUpdateCardSuccess() throws Exception {
        Card updated = new Card("Lightning Bolt", "Instant", "3 damage", "Red", 1, 0, 0, null);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(ArgumentMatchers.any(Card.class))).thenReturn(updated);

        mockMvc.perform(put("/api/cards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Lightning Bolt"));
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void testUpdateCardNotFound() throws Exception {
        when(cardRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/cards/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(card)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void testDeleteCardSuccess() throws Exception {
        when(cardRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/cards/remove/1"))
                .andExpect(status().isOk());
        verify(cardRepository).deleteById(1L);
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void testDeleteCardNotFound() throws Exception {
        when(cardRepository.existsById(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/cards/remove/99"))
                .andExpect(status().isNotFound());
    }
}
