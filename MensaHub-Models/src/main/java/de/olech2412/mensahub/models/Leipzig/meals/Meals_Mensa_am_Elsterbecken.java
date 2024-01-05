package de.olech2412.mensahub.models.Leipzig.meals;

import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_am_Elsterbecken;
import de.olech2412.mensahub.models.Meal;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents the meals_mensa_am_elsterbecken entity in the database.
 */
@Getter
@Setter
@Entity
@Table(name = "meals_mensa_am_elsterbecken")
public class Meals_Mensa_am_Elsterbecken extends Meal {

    @ManyToOne
    @JoinColumn(name = "mensa_am_elsterbecken_id", nullable = false)
    private Mensa_am_Elsterbecken mensa_am_elsterbecken;

    public Meals_Mensa_am_Elsterbecken() {

    }

}
