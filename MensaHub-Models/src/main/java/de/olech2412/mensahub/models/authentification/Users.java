package de.olech2412.mensahub.models.authentification;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents the users entity in the database.
 * This user class is used by MensaHub-Junction to authenticate users.
 */
@Entity
@Getter
@Setter
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", nullable = false)
    private Long userId; // primary key

    @Column(name = "username", nullable = false, unique = true)
    private String username; // the username will be used for the login

    @Column(name = "password", nullable = false)
    private String password; // the password will be used for the login

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role; // the role of the user

    @Column(name = "enabled", nullable = false)
    private Boolean enabled; // if the user is enabled

    @Column(name = "proponent", nullable = false)
    private Boolean proponent;

    /**
     * Constructor for the user.
     * @param username the username
     * @param password the password
     * @param role the role
     * @param enabled if the user is enabled
     */
    public Users(String username, String password, Role role, boolean enabled, boolean proponent) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
        this.proponent = proponent;
    }

    /**
     * Default constructor for the user.
     */
    public Users() {

    }
}
