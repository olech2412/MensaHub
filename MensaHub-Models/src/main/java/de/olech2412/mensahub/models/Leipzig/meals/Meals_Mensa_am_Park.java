package de.olech2412.mensahub.models.Leipzig.meals;

import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_am_Park;
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
@Table(name = "meals_mensa_am_park")
public class Meals_Mensa_am_Park extends Meal {

    @ManyToOne
    @JoinColumn(name = "mensa_am_park_id", nullable = false)
    private Mensa_am_Park mensa_am_park;

    public Meals_Mensa_am_Park() {

    }

}
