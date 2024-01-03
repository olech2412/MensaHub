package de.olech2412.mensahub.models.Leipzig.meals;

import de.olech2412.mensahub.models.Leipzig.mensen.Mensa_am_Medizincampus;
import de.olech2412.mensahub.models.Meal;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "meals_mensa_am_medizincampus")
public class Meals_Mensa_am_Medizincampus extends Meal {

    @ManyToOne
    @JoinColumn(name = "mensa_am_medizincampus_id", nullable = false)
    private Mensa_am_Medizincampus mensa_am_medizincampus;

    public Meals_Mensa_am_Medizincampus() {

    }

}
