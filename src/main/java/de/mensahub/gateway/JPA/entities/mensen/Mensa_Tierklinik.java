package de.mensahub.gateway.JPA.entities.mensen;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import de.mensahub.gateway.JPA.entities.MailUser;
import de.mensahub.gateway.JPA.entities.meals.Meals_Mensa_Tierklinik;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "mensa_tierklinik")
/**
 * Used to create a Mensa Tierklinik
 */
public class Mensa_Tierklinik extends Mensa {

    @OneToMany(mappedBy = "mensa_tierklinik", fetch = FetchType.LAZY)
    @JsonIgnore
    @Transient
    private Set<Meals_Mensa_Tierklinik> meals_mensa_tierklinik; // Many Meals can be in one Cafeteria

    @OneToMany(mappedBy = "mensa_tierklinik", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    @Transient
    private Set<MailUser> mail_users; // Many MailUsers can be in one Cafeteria
}