package de.olech2412.mensahub.models.authentification;


import de.olech2412.mensahub.models.Leipzig.mensen.*;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * This class represents the mail-user entity in the database.
 */
@Getter
@Setter
@Entity
@Table(name = "mail_users")
public class MailUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id; // primary key

    @Column(name = "email", nullable = false, unique = true)
    private String email; // the email from the user

    @OneToOne
    private ActivationCode activationCode; // the activation code for the user

    @OneToOne
    private DeactivationCode deactivationCode; // the deactivation code for the user

    private String firstname; // the firstname from the user

    private String lastname; // the lastname from the user

    private boolean enabled; // if the user is enabled

    private LocalDate deactivatedUntil; // the date until the user is deactivated

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cafeteria_dittrichring_id")
    private Cafeteria_Dittrichring cafeteria_dittrichring; // the cafeteria dittrichring

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensa_academica_id")
    private Mensa_Academica mensa_academica; // the mensa academica

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensa_schoenauer_str_id")
    private Mensa_Schoenauer_Str mensa_schoenauer_str; // the mensa schoenauer str

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensa_am_elsterbecken_id")
    private Mensa_am_Elsterbecken mensa_am_elsterbecken; // the mensa am elsterbecken

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensa_am_medizincampus_id")
    private Mensa_am_Medizincampus mensa_am_medizincampus; // the mensa am medizincampus

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensa_am_park_id")
    private Mensa_am_Park mensa_am_park; // the mensa am park

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensa_peterssteinweg_id")
    private Mensa_Peterssteinweg mensa_peterssteinweg; // the mensa peterssteinweg

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensa_tierklinik_id")
    private Mensa_Tierklinik mensa_tierklinik; // the mensa tierklinik

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menseria_am_botanischen_garten_id")
    private Menseria_am_Botanischen_Garten menseria_am_botanischen_garten; // the menseria am botanischen garten

    /**
     * Default constructor for the mail-user.
     */
    public MailUser() {
    }

    /**
     * Constructor for the mail-user.
     * @param email the email
     * @param firstname the firstname
     * @param lastname the lastname
     * @param enabled if the user is enabled
     */
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