package de.olech2412.mensahub.models;


import de.olech2412.mensahub.models.Leipzig.mensen.*;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "mail_users")
public class MailUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @OneToOne
    private ActivationCode activationCode;

    @OneToOne
    private DeactivationCode deactivationCode;
    private String firstname;
    private String lastname;
    private boolean enabled;

    private LocalDate deactviatedUntil;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cafeteria_dittrichring_id")
    private Cafeteria_Dittrichring cafeteria_dittrichring;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensa_academica_id")
    private Mensa_Academica mensa_academica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensa_schoenauer_str_id")
    private Mensa_Schoenauer_Str mensa_schoenauer_str;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensa_am_elsterbecken_id")
    private Mensa_am_Elsterbecken mensa_am_elsterbecken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensa_am_medizincampus_id")
    private Mensa_am_Medizincampus mensa_am_medizincampus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensa_am_park_id")
    private Mensa_am_Park mensa_am_park;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensa_peterssteinweg_id")
    private Mensa_Peterssteinweg mensa_peterssteinweg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mensa_tierklinik_id")
    private Mensa_Tierklinik mensa_tierklinik;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menseria_am_botanischen_garten_id")
    private Menseria_am_Botanischen_Garten menseria_am_botanischen_garten;

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