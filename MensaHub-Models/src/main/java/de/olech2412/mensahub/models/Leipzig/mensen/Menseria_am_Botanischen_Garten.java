package de.olech2412.mensahub.models.Leipzig.mensen;

import de.olech2412.mensahub.models.Leipzig.meals.Meals_Menseria_am_Botanischen_Garten;
import de.olech2412.mensahub.models.MailUser;
import de.olech2412.mensahub.models.Mensa;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "menseria_am_botanischen_garten")
public class Menseria_am_Botanischen_Garten extends Mensa {

    @OneToMany(mappedBy = "menseria_am_botanischen_garten")
    private Set<Meals_Menseria_am_Botanischen_Garten> meals_menseria_am_botanischen_garten;

    @OneToMany(mappedBy = "menseria_am_botanischen_garten", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<MailUser> mail_users;
}