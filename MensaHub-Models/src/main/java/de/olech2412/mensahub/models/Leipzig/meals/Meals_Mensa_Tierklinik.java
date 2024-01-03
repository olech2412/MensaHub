package de.olech2412.mensahub.models.Leipzig.meals;

import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_Tierklinik;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "meals_mensa_tierklinik")
public class Meals_Mensa_Tierklinik extends Meal {

    @ManyToOne
    @JoinColumn(name = "mensa_tierklinik_id", nullable = false)
    private Mensa_Tierklinik mensa_tierklinik;

    public Meals_Mensa_Tierklinik() {

    }

}
