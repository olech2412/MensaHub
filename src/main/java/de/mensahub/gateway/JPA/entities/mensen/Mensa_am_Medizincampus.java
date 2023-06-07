package de.mensahub.gateway.JPA.entities.mensen;

import de.mensahub.gateway.JPA.entities.MailUser;
import de.mensahub.gateway.JPA.entities.meals.Meals_Mensa_am_Medizincampus;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "mensa_am_medizincampus")
public class Mensa_am_Medizincampus extends Mensa {

    @OneToMany(mappedBy = "mensa_am_medizincampus")
    private Set<Meals_Mensa_am_Medizincampus> meals_mensa_am_medizincampus;

    @OneToMany(mappedBy = "mensa_am_medizincampus", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<MailUser> mail_users;
}