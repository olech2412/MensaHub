package com.example.demo.JPA;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "meals")
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private String name;
    private String description;
    private String price;
    private String category;
    private LocalDate creationDate;

    private Integer responseCode;

    public Meal() {
    }

    public Meal(String name, String description, String price, String category, LocalDate creationDate) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.creationDate = creationDate;
    }

    @Override
    public String toString() {
        return "Meal: " + "name=" + name + ", description=" + description + ", price=" + price + ", category=" + category + ", creationDate=" + creationDate;
    }
}
