package de.olech2412.mensahub.gateway.JPA.entities.meals;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.mensahub.gateway.JPA.entities.mensen.Mensa_am_Medizincampus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "meals_mensa_am_medizincampus")
/**
 * Used to create a meal for Mensa am Medizincampus
 */
public class Meals_Mensa_am_Medizincampus extends Meal {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensa_am_medizincampus_id", nullable = false)
    @JsonIgnore
    private Mensa_am_Medizincampus mensa_am_medizincampus; // Many Meals can be in one Cafeteria

    public Meals_Mensa_am_Medizincampus() {

    }

}
