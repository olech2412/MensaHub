package com.essensGetter.api.JPA.entities.meals;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
@MappedSuperclass
public abstract class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Exclude
    @Column(name = "id", nullable = false)
    protected Long id;
    protected String name;
    private String description;
    private String price;
    private String category;
    private LocalDate servingDate;

    private Integer responseCode;
    private Double rating = 0.0;
    private Integer votes = 0;
    private Integer starsTotal = 0;

    public Meal() {
    }

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
        return "Meal: " + "name=" + name + ", description=" + description + ", price=" + price + ", category=" + category + ", servingDate=" + servingDate + ", rating=" + rating + ", votes=" + votes + ", starsTotal=" + starsTotal + ", votes=" + votes;
    }
}
