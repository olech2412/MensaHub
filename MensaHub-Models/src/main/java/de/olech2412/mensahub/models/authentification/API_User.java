package de.olech2412.mensahub.models.authentification;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * This class represents the API_User entity in the database.
 *
 * @since 1.0.0
 * @author olech2412
 */
@Entity
@Getter
@Setter
public class API_User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "api_user_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // only write the id to the json
    private Long apiUserId; // primary key

    @Column(name = "username", nullable = false, unique = true)
    @NotEmpty(message = "A username is required")
    @NotNull
    @Size(min = 2, max = 100, message = "The length of full name must be between 2 and 100 characters")
    private String apiUsername; // the username will be used for the login

    @Column(name = "email", nullable = false)
    @NotEmpty(message = "Email is required")
    @NotNull
    @Email(message = "The email is invalid.", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String email; // email to contact the user

    @Column(name = "password", nullable = false)
    @NotEmpty
    @NotNull
    @Size(min = 8, max = 255, message = "The length for the password must be between 8 and 255 characters")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password; // the password will be used for the login

    @Column(name = "enabled_by_admin", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    private Boolean enabledByAdmin; // if the user is enabled by an admin

    @Column(name = "verified_email", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    private Boolean verified_email; // if the email is verified

    @Column(name = "description", nullable = false)
    @NotEmpty(message = "Description is required")
    @NotNull
    @Size(min = 10, max = 255, message = "The length must be between 10 and 255 characters")
    private String description; // a description of the user

    @Column(name = "role", nullable = false)
    @NotEmpty
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Enumerated(EnumType.STRING)
    private Role role = Role.API_USER; // default value

    @Column(name = "creationDate", nullable = false)
    private LocalDate creationDate = LocalDate.now(); // default value

    @Column(name = "blocking_reason")
    private String blockingReason; // the reason why the user is blocked

    @OneToOne
    private ActivationCode activationCode; // one-to-one relationship with ActivationCode needed for activation

    @OneToOne
    private DeactivationCode deactivationCode; // one-to-one relationship with DeactivationCode needed for deactivation

    @Column(name = "last_login")
    private LocalDateTime lastLogin; // the last login of the user

    public API_User() { // default constructor

    }
}