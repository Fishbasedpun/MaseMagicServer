import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table
public class Card {
	private String name, typeLine, text, coulor, imageURL;
	private int  cost, power, toughness;
	public Card(String name, String typeLine, String text, String color, int cost, int power, int toughness) {
        this.name = name;
        this.typeLine = typeLine;
        this.text = text;
        this.color = color;
        this.cost = cost;
        this.power = power;
        this.toughness = toughness;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getTypeLine() {
        return typeLine;
    }

    public String getText() {
        return text;
    }

    public String getColor() {
        return color;
    }
    
    public String getImageURL() {
        return imageURL;
    }

    public int getCost() {
        return cost;
    }

    public int getPower() {
        return power;
    }

    public int getToughness() {
        return toughness;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setTypeLine(String typeLine) {
        this.typeLine = typeLine;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setColor(String color) {
        this.color = color;
    }
    
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public void setToughness(int toughness) {
        this.toughness = toughness;
    }
}
