package com.tus.magic.user_manager.service;

import com.tus.magic.models.Card;
import com.tus.magic.user_manager.models.User;
import com.tus.magic.repository.CardRepository;
import com.tus.magic.user_manager.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class FavoriteService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    public boolean addFavoriteCard(String username, Long cardId) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        Optional<Card> cardOptional = cardRepository.findById(cardId);

        if (userOptional.isPresent() && cardOptional.isPresent()) {
            User user = userOptional.get();
            Card card = cardOptional.get();

            user.addFavoriteCard(card);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public boolean removeFavoriteCard(String username, Long cardId) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        Optional<Card> cardOptional = cardRepository.findById(cardId);

        if (userOptional.isPresent() && cardOptional.isPresent()) {
            User user = userOptional.get();
            Card card = cardOptional.get();

            user.removeFavoriteCard(card);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
