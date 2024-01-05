package de.olech2412.mensahub.models.Leipzig;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents the leipzig_allergene entity in the database.
 */
@Getter
@Setter
@Entity
@Table(name = "leipzig_allergene")
public class Allergene {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id; // this is the primary key

    private String allergen; // this is the name of the allergen

    private String token; // this is the token of the allergen

    public Allergene() {

    }

}

