package de.mensahub.gateway.JPA.entities;

import de.mensahub.gateway.JPA.entities.mensen.*;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "mail_users")
/**
 * Used to create a MailUser
 * A MailUser is a user who has subscribed to a Mensa via MensaHub-Junction
 */
public class MailUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @OneToOne
    private ActivationCode activationCode; // One ActivationCode can be used by one MailUser

    @OneToOne
    private DeactivationCode deactivationCode; // One DeactivationCode can be used by one MailUser
    private String firstname;
    private String lastname;
    private boolean enabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cafeteria_dittrichring_id")
    private Cafeteria_Dittrichring cafeteria_dittrichring; // Many Cafeterias can be in one MailUser

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensa_academica_id")
    private Mensa_Academica mensa_academica; // Many Mensas can be in one MailUser

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensa_schoenauer_str_id")
    private Mensa_Schoenauer_Str mensa_schoenauer_str; // Many Mensas can be in one MailUser

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensa_am_elsterbecken_id")
    private Mensa_am_Elsterbecken mensa_am_elsterbecken; // Many Mensas can be in one MailUser

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensa_am_medizincampus_id")
    private Mensa_am_Medizincampus mensa_am_medizincampus; // Many Mensas can be in one MailUser

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensa_am_park_id")
    private Mensa_am_Park mensa_am_park; // Many Mensas can be in one MailUser

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensa_peterssteinweg_id")
    private Mensa_Peterssteinweg mensa_peterssteinweg; // Many Mensas can be in one MailUser

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensa_tierklinik_id")
    private Mensa_Tierklinik mensa_tierklinik; // Many Mensas can be in one MailUser

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menseria_am_botanischen_garten_id")
    private Menseria_am_Botanischen_Garten menseria_am_botanischen_garten; // Many Mensas can be in one MailUser

    public MailUser() {
    }

    public MailUser(String email, String firstname, String lastname,
                    boolean enabled) {
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "User{" +
                ", email='" + email + '\'' +
                '}';
    }
}