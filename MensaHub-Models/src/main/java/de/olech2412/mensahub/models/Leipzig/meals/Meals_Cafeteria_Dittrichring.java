package de.olech2412.mensahub.models.Leipzig.meals;

import de.olech2412.mensahub.models.Leipzig.mensen.Cafeteria_Dittrichring;
import de.olech2412.mensahub.models.Meal;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents the meals_cafeteria_dittrichring entity in the database.
 */
@Getter
@Setter
@Entity
@Table(name = "meals_cafeteria_dittrichring")
public class Meals_Cafeteria_Dittrichring extends Meal {

    @ManyToOne
    @JoinColumn(name = "cafeteria_dittrichring_id", nullable = false)
    private Cafeteria_Dittrichring cafeteria_dittrichring;

    public Meals_Cafeteria_Dittrichring() {

    }

}
