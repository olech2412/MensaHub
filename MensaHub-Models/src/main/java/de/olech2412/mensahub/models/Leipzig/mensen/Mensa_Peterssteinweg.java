package de.olech2412.mensahub.models.Leipzig.mensen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.olech2412.mensahub.models.Leipzig.meals.Meals_Mensa_Peterssteinweg;
import de.olech2412.mensahub.models.MailUser;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "mensa_peterssteinweg")
public class Mensa_Peterssteinweg extends Mensa {

    @OneToMany(mappedBy = "mensa_peterssteinweg")
    @JsonIgnore
    @Transient
    private Set<Meals_Mensa_Peterssteinweg> meals_mensa_peterssteinweg;

    @OneToMany(mappedBy = "mensa_peterssteinweg", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnore
    @Transient
    private Set<MailUser> mail_users;
}