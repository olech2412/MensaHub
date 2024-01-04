package de.olech2412.mensahub.models.Leipzig.mensen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.olech2412.mensahub.models.Leipzig.meals.Meals_Schoenauer_Str;
import de.olech2412.mensahub.models.authentification.MailUser;
import de.olech2412.mensahub.models.Mensa;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "mensa_schoenauer_str")
public class Mensa_Schoenauer_Str extends Mensa {

    @OneToMany(mappedBy = "mensa_schoenauer_str")
    @JsonIgnore
    @Transient
    private Set<Meals_Schoenauer_Str> meals_schoenauer_strList;

    @OneToMany(mappedBy = "mensa_schoenauer_str", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonIgnore
    @Transient
    private Set<MailUser> mail_users;
}