package de.olech2412.mensahub.models.authentification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.olech2412.mensahub.models.Mensa;
import de.olech2412.mensahub.models.Preferences;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class represents the mail-user entity in the database.
 */
@Getter
@Setter
@Entity
@Table(name = "mail_users")
public class MailUser {
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "mail_user_mensa_abbo",
            joinColumns = {@JoinColumn(name = "mail_users_id")},
            inverseJoinColumns = {@JoinColumn(name = "mensas_id")}
    )
    @JsonIgnore
    Set<Mensa> mensas = new HashSet<>();
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
    private boolean wantsUpdate; // if the user wants to get update mails if meals changed
    private boolean wantsCollaborationInfoMail; // if the user wants to get mails about collaborations only
    @OneToOne(cascade = CascadeType.ALL)
    private Preferences preferences;
    private boolean pushNotificationsEnabled; // user wants push notifications?
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "subscription_id")
    private List<SubscriptionEntity> subscriptions = new ArrayList<>();

    /**
     * Default constructor for the mail-user.
     */
    public MailUser() {
    }

    /**
     * Constructor for the mail-user.
     *
     * @param email     the email
     * @param firstname the firstname
     * @param lastname  the lastname
     * @param enabled   if the user is enabled
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