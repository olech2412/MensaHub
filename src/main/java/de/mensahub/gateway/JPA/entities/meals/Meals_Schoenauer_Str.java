package de.mensahub.gateway.JPA.entities.meals;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.mensahub.gateway.JPA.entities.mensen.Mensa_Schoenauer_Str;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "meals_schoenauer_str")
/**
 * Used to create a meal for Mensa Schoenauer Str
 */
public class Meals_Schoenauer_Str extends Meal {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensa_schoenauer_str_id", nullable = false)
    @JsonIgnore
    private Mensa_Schoenauer_Str mensa_schoenauer_str; // Many Meals can be in one Cafeteria

    public Meals_Schoenauer_Str() {

    }

}
