package com.tus.magic.models;
import java.util.HashSet;
import java.util.Set;

import com.tus.magic.user_manager.models.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "cards")
public class Card {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    @Column(name = "typeLine", nullable = false) // ✅ Explicitly set column name
    private String typeLine;
    private String text;
    private String color;
    private int cost;
    private int power;
    private int toughness;
    @Column(name = "image")
    private String imagePath; 
    
    @ManyToMany(mappedBy = "favoriteCards")
    private Set<User> favoritedByUsers = new HashSet<>();

    public Card() {}

    public Card(String name, String typeLine, String text, String color, int cost, int power, int toughness, String imagePath) {
        this.name = name;
        this.typeLine = typeLine;
        this.text = text;
        this.color = color;
        this.cost = cost;
        this.power = power;
        this.toughness = toughness;
        this.imagePath = imagePath;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getTypeLine() { return typeLine; }
    public void setTypeLine(String typeLine) { this.typeLine = typeLine; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }
    public int getPower() { return power; }
    public void setPower(int power) { this.power = power; }
    public int getToughness() { return toughness; }
    public void setToughness(int toughness) { this.toughness = toughness; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}
