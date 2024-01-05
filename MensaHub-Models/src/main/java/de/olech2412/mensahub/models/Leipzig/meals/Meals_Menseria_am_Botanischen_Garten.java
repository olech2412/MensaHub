package de.olech2412.mensahub.models.Leipzig.meals;

import de.olech2412.mensahub.models.Leipzig.mensen.Menseria_am_Botanischen_Garten;
import de.olech2412.mensahub.models.Meal;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents the meals_menseria_am_botanischen_garten entity in the database.
 */
@Getter
@Setter
@Entity
@Table(name = "meals_menseria_am_botanischen_garten")
public class Meals_Menseria_am_Botanischen_Garten extends Meal {

    @ManyToOne
    @JoinColumn(name = "menseria_am_botanischen_garten_id", nullable = false)
    private Menseria_am_Botanischen_Garten menseria_am_botanischen_garten;

    public Meals_Menseria_am_Botanischen_Garten() {

    }

}
