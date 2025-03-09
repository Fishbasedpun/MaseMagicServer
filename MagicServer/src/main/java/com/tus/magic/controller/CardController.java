package com.tus.magic.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tus.magic.models.Card;
import com.tus.magic.repository.CardRepository;

@RestController
@RequestMapping("/api/cards")
@CrossOrigin(origins = "*")
public class CardController {

    @Autowired
    private CardRepository cardRepository;

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @PostMapping
    public Card createCard(
            @RequestParam("name") String name,
            @RequestParam("typeLine") String typeLine,
            @RequestParam("text") String text,
            @RequestParam("color") String color,
            @RequestParam("cost") int cost,
            @RequestParam("power") int power,
            @RequestParam("toughness") int toughness,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        String imagePath = null;
        
        if (image != null && !image.isEmpty()) {
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs(); 
            }

            imagePath = UPLOAD_DIR + image.getOriginalFilename();
            File destinationFile = new File(imagePath);

            image.transferTo(destinationFile); 
        }

        Card card = new Card(name, typeLine, text, color, cost, power, toughness, imagePath);
        return cardRepository.save(card);
    }


    @GetMapping
    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Card> updateCard(@PathVariable Long id, @RequestBody Card updatedCard) {
        return cardRepository.findById(id)
            .map(card -> {
                card.setName(updatedCard.getName());
                card.setTypeLine(updatedCard.getTypeLine());
                card.setText(updatedCard.getText());
                card.setColor(updatedCard.getColor());
                card.setCost(updatedCard.getCost());
                card.setPower(updatedCard.getPower());
                card.setToughness(updatedCard.getToughness());
                return ResponseEntity.ok(cardRepository.save(card));
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        if (cardRepository.existsById(id)) {
            cardRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}