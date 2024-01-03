package de.olech2412.mensahub.models.Leipzig.meals;

import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_Academica;
import de.olech2412.mensahub.models.Meal;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "meals_mensa_academica")
public class Meals_Mensa_Academica extends Meal {

    @ManyToOne
    @JoinColumn(name = "mensa_academica_id", nullable = false)
    private Mensa_Academica mensa_academica;

    public Meals_Mensa_Academica() {

    }

}