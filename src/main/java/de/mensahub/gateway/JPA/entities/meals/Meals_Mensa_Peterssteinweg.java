package de.mensahub.gateway.JPA.entities.meals;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.mensahub.gateway.JPA.entities.mensen.Mensa_Peterssteinweg;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "meals_mensa_peterssteinweg")
/**
 * Used to create a meal for Mensa Peterssteinweg
 */
public class Meals_Mensa_Peterssteinweg extends Meal {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensa_peterssteinweg_id", nullable = false)
    @JsonIgnore
    private Mensa_Peterssteinweg mensa_peterssteinweg; // Many Meals can be in one Cafeteria

    public Meals_Mensa_Peterssteinweg() {

    }

}
