package de.mensahub.gateway.JPA.entities.meals;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.mensahub.gateway.JPA.entities.mensen.Mensa_Tierklinik;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "meals_mensa_tierklinik")
/**
 * Used to create a meal for Mensa Tierklinik
 */
public class Meals_Mensa_Tierklinik extends Meal {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensa_tierklinik_id", nullable = false)
    @JsonIgnore
    private Mensa_Tierklinik mensa_tierklinik; // Many Meals can be in one Cafeteria

    public Meals_Mensa_Tierklinik() {

    }

}
