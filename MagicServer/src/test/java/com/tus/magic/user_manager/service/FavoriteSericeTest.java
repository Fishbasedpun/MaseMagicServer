package com.tus.magic.user_manager.service;

import com.tus.magic.models.Card;
import com.tus.magic.user_manager.models.User;
import com.tus.magic.repository.CardRepository;
import com.tus.magic.user_manager.repo.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FavoriteServiceTest {

    private FavoriteService favoriteService;
    private UserRepository userRepository;
    private CardRepository cardRepository;

    private User user;
    private Card card;

    @BeforeEach
    void setUp() throws Exception {
        favoriteService = new FavoriteService();

        // Mock dependencies
        userRepository = mock(UserRepository.class);
        cardRepository = mock(CardRepository.class);

        injectPrivateField(favoriteService, "userRepository", userRepository);
        injectPrivateField(favoriteService, "cardRepository", cardRepository);

        user = new User("john", "Password123", null);
        card = new Card();
        card.setId(1L);
        card.setName("Sample Card");
    }

    @Test
    void testAddFavoriteCardSuccess() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        boolean result = favoriteService.addFavoriteCard("john", 1L);
        assertTrue(result);
        assertTrue(user.getFavoriteCards().contains(card));
        verify(userRepository).save(user);
    }

    @Test
    void testAddFavoriteCardFailWhenUserOrCardNotFound() {
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());

        boolean result = favoriteService.addFavoriteCard("john", 1L);
        assertFalse(result);
    }

    @Test
    void testRemoveFavoriteCardSuccess() {
        user.addFavoriteCard(card);
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        boolean result = favoriteService.removeFavoriteCard("john", 1L);
        assertTrue(result);
        assertFalse(user.getFavoriteCards().contains(card));
        verify(userRepository).save(user);
    }

    @Test
    void testRemoveFavoriteCardFailWhenUserOrCardNotFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        boolean result = favoriteService.removeFavoriteCard("john", 1L);
        assertFalse(result);
    }

    private void injectPrivateField(Object target, String fieldName, Object toInject) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, toInject);
    }
}
