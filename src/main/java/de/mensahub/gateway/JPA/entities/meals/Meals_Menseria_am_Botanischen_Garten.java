package de.mensahub.gateway.JPA.entities.meals;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.mensahub.gateway.JPA.entities.mensen.Menseria_am_Botanischen_Garten;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "meals_menseria_am_botanischen_garten")
/**
 * Used to create a meal for Menseria am Botanischen Garten
 */
public class Meals_Menseria_am_Botanischen_Garten extends Meal {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menseria_am_botanischen_garten_id", nullable = false)
    @JsonIgnore
    private Menseria_am_Botanischen_Garten menseria_am_botanischen_garten; // Many Meals can be in one Cafeteria

    public Meals_Menseria_am_Botanischen_Garten() {

    }

}
