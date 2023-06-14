package de.mensahub.gateway.JPA.entities.mensen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.mensahub.gateway.JPA.entities.MailUser;
import de.mensahub.gateway.JPA.entities.meals.Meals_Cafeteria_Dittrichring;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "cafeteria_dittrichring")
/**
 * Used to create a Cafeteria Dittrichring
 */
public class Cafeteria_Dittrichring extends Mensa {

    @OneToMany(mappedBy = "cafeteria_dittrichring")
    @JsonIgnore
    private Set<Meals_Cafeteria_Dittrichring> meals_cafeteria_dittrichrings; // Many Meals can be in one Cafeteria

    @OneToMany(mappedBy = "cafeteria_dittrichring", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<MailUser> mail_users; // Many MailUsers can be in one Cafeteria

}