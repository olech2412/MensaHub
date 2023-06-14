package de.mensahub.gateway.JPA.entities.meals;

import de.mensahub.gateway.JPA.entities.mensen.Mensa_Schoenauer_Str;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Setter
@Getter
@Entity
@Table(name = "meals_schoenauer_str")
/**
 * Used to create a meal for Mensa Schoenauer Str
 */
public class Meals_Schoenauer_Str extends Meal {

    @ManyToOne
    @JoinColumn(name = "mensa_schoenauer_str_id", nullable = false)
    @JsonIgnore
    private Mensa_Schoenauer_Str mensa_schoenauer_str; // Many Meals can be in one Cafeteria

    public Meals_Schoenauer_Str() {

    }

}
