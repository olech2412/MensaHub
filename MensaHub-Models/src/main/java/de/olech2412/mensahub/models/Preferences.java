package de.olech2412.mensahub.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Preferences {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "disliked_categories", joinColumns = @JoinColumn(name = "disliked_categories_id"))
    @Column(name = "disliked_categories", nullable = false)
    private List<String> disliked_categories = new ArrayList<>();

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "avoided_allergens", joinColumns = @JoinColumn(name = "avoided_allergens_id"))
    @Column(name = "avoided_allergens", nullable = false)
    private List<String> avoidedAllergens = new ArrayList<>();

    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "disliked_ingredients", joinColumns = @JoinColumn(name = "disliked_ingredients_id"))
    @Column(name = "disliked_ingedrients", nullable = false)
    private List<String> dislikedIngredients = new ArrayList<>();
}
