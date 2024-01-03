package de.olech2412.mensahub.models.Leipzig.mensen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.olech2412.mensahub.models.Leipzig.meals.Meals_Cafeteria_Dittrichring;
import de.olech2412.mensahub.models.MailUser;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "cafeteria_dittrichring")
public class Cafeteria_Dittrichring extends Mensa {

    @OneToMany(mappedBy = "cafeteria_dittrichring")
    @JsonIgnore
    @Transient
    private Set<Meals_Cafeteria_Dittrichring> meals_cafeteria_dittrichrings;

    @OneToMany(mappedBy = "cafeteria_dittrichring", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnore
    @Transient
    private Set<MailUser> mail_users;

}