package de.olech2412.mensahub.models;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * This class represents the meal it is the super class for all meals.
 */
@Getter
@Setter
@EqualsAndHashCode
@MappedSuperclass
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Exclude
    @Column(name = "id", nullable = false)
    protected Long id; // this is the primary key

    protected String name; // this is the name of the meal

    private String description; // this is the description of the meal

    private String price; // this is the price of the meal

    private String category; // this is the category of the meal

    private LocalDate servingDate; // this is the date the meal is served

    private String additionalInfo; // this is additional information about the meal

    private String allergens; // this is the allergens of the meal

    private String additives; // this is the additives of the meal

    @EqualsAndHashCode.Exclude
    private Double rating = 0.0; // this is the rating of the meal

    @EqualsAndHashCode.Exclude
    private Integer votes = 0; // this is the number of votes for the meal

    @EqualsAndHashCode.Exclude
    private Integer starsTotal = 0; // this is the number of stars for the meal

    /**
     * This is the default constructor.
     */
    public Meal() {
    }

    /**
     * This is the constructor with all parameters.
     * @param name the name of the meal
     * @param description the description of the meal
     * @param price the price of the meal
     * @param category the category of the meal
     * @param servingDate the date the meal is served
     */
    public Meal(String name, String description, String price, String category, LocalDate servingDate) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.servingDate = servingDate;
        this.rating = 0.0;
        this.votes = 0;
        this.starsTotal = 0;
    }

    @Override
    public String toString() {
        return "Meal: " + "name=" + name + ", description=" + description + ", price=" + price + ", category=" + category + ", additionalInfo=" + additionalInfo + ", allergens=" + allergens + ", servingDate=" + servingDate + ", rating=" + rating + ", votes=" + votes + ", starsTotal=" + starsTotal + ", votes=" + votes;
    }
}
