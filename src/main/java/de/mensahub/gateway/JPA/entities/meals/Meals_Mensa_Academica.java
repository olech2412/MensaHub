package de.mensahub.gateway.JPA.entities.meals;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.mensahub.gateway.JPA.entities.mensen.Mensa_Academica;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "meals_mensa_academica")
/**
 * Used to create a meal for Mensa Academica
 */
public class Meals_Mensa_Academica extends Meal {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensa_academica_id", nullable = false)
    @JsonIgnore
    private Mensa_Academica mensa_academica; // Many Meals can be in one Cafeteria

    public Meals_Mensa_Academica() {

    }

}