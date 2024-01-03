package de.olech2412.mensahub.datadispatcher.JPA.entities.Leipzig;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "leipzig_allergene")
public class Allergene {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    private String allergen;

    private String token;

    public Allergene() {

    }

}

