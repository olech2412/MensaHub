package de.olech2412.mensahub.models.Leipzig.meals;

import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_Schoenauer_Str;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Setter
@Getter
@Entity
@Table(name = "meals_schoenauer_str")
public class Meals_Schoenauer_Str extends Meal {

    @ManyToOne
    @JoinColumn(name = "mensa_schoenauer_str_id", nullable = false)
    private Mensa_Schoenauer_Str mensa_schoenauer_str;

    public Meals_Schoenauer_Str() {

    }

}
