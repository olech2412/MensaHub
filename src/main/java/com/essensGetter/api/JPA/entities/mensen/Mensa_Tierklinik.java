package com.essensGetter.api.JPA.entities.mensen;

import com.essensGetter.api.JPA.entities.MailUser;
import com.essensGetter.api.JPA.entities.meals.Meals_Mensa_Tierklinik;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "mensa_tierklinik")
public class Mensa_Tierklinik extends Mensa {

    @OneToMany(mappedBy = "mensa_tierklinik")
    private Set<Meals_Mensa_Tierklinik> meals_mensa_tierklinik;

    @OneToMany(mappedBy = "mensa_tierklinik", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<MailUser> mail_users;
}