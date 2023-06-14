package de.mensahub.gateway.JPA.entities.meals;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.mensahub.gateway.JPA.entities.mensen.Mensa_am_Park;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "meals_mensa_am_park")
/**
 * Used to create a meal for Mensa am Park
 */
public class Meals_Mensa_am_Park extends Meal {

    @ManyToOne
    @JoinColumn(name = "mensa_am_park_id", nullable = false)
    @JsonIgnore
    private Mensa_am_Park mensa_am_park; // Many Meals can be in one Cafeteria

    public Meals_Mensa_am_Park() {

    }

}
