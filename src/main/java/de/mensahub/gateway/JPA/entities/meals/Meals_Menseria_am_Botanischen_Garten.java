package de.mensahub.gateway.JPA.entities.meals;

import de.mensahub.gateway.JPA.entities.mensen.Menseria_am_Botanischen_Garten;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "meals_menseria_am_botanischen_garten")
public class Meals_Menseria_am_Botanischen_Garten extends Meal {

    @ManyToOne
    @JoinColumn(name = "menseria_am_botanischen_garten_id", nullable = false)
    @JsonIgnore
    private Menseria_am_Botanischen_Garten menseria_am_botanischen_garten;

    public Meals_Menseria_am_Botanischen_Garten() {

    }

}
