package de.mensahub.gateway.JPA.entities.authentication;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
/**
 * This class is used to store the user credentials in the database.
 * This is the user entity to login into MensaHub-Junction
 */
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", nullable = false)
    private Long userId; // primary key

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    /**
     * Constructor for the Users class.
     * Not used because it is not needed for the API
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @param role     The role of the user.
     * @param enabled  Whether the user is enabled or not.
     */
    public Users(String username, String password, String role, Boolean enabled) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
    }

    public Users() {

    }
}
