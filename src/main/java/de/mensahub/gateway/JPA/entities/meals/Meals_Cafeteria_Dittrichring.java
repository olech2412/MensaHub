package de.mensahub.gateway.JPA.entities.meals;

import de.mensahub.gateway.JPA.entities.mensen.Cafeteria_Dittrichring;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "meals_cafeteria_dittrichring")
/**
 * Used to create a meal for Cafeteria Dittrichring
 */
public class Meals_Cafeteria_Dittrichring extends Meal {

    @ManyToOne
    @JoinColumn(name = "cafeteria_dittrichring_id", nullable = false)
    @JsonIgnore
    private Cafeteria_Dittrichring cafeteria_dittrichring; // Many Meals can be in one Cafeteria

    public Meals_Cafeteria_Dittrichring() {

    }

}
