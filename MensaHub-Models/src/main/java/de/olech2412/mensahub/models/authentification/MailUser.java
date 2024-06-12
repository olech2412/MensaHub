package de.olech2412.mensahub.models.authentification;

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

    private LocalDate deactviatedUntil; // the date until the user is deactivated

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