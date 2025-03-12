package com.tus.magic.user_manager.controller;

import com.tus.magic.models.Card;
import com.tus.magic.user_manager.models.User;
import com.tus.magic.user_manager.repo.UserRepository;
import com.tus.magic.user_manager.service.FavoriteService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add")
    public ResponseEntity<String> addFavorite(@RequestParam String username, @RequestParam Long cardId) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (favoriteService.addFavoriteCard(user.getUsername(), cardId)) {
                return ResponseEntity.ok("Card added to favorites.");
            }
        }
        return ResponseEntity.badRequest().body("User or Card not found.");
    }

    @DeleteMapping("/remove")
    public ResponseEntity<String> removeFavorite(@RequestParam String username, @RequestParam Long cardId) {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (favoriteService.removeFavoriteCard(user.getUsername(), cardId)) {
                return ResponseEntity.ok("Card removed from favorites.");
            }
        }
        return ResponseEntity.badRequest().body("User or Card not found.");
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Integer>> getFavoriteStats() {
        List<User> users = userRepository.findAll();
        Map<String, Integer> cardCounts = new HashMap<>();

        for (User user : users) {
            for (Card card : user.getFavoriteCards()) {
                cardCounts.put(card.getName(), cardCounts.getOrDefault(card.getName(), 0) + 1);
            }
        }

        return ResponseEntity.ok(cardCounts);
    }
}
