package de.mensahub.gateway.JPA.entities.authentication;

import de.mensahub.gateway.JPA.entities.ActivationCode;
import de.mensahub.gateway.JPA.entities.DeactivationCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
/**
 * This class represents the API_User entity in the database.
 */
public class API_User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "api_user_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long apiUserId; // primary key

    @Column(name = "username", nullable = false, unique = true)
    @NotEmpty(message = "A username is required")
    @NotNull
    @Size(min = 2, max = 100, message = "The length of full name must be between 2 and 100 characters")
    private String apiUsername;

    @Column(name = "email", nullable = false)
    @NotEmpty(message = "Email is required")
    @NotNull
    @Email(message = "The email is invalid.", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String email;

    @Column(name = "password", nullable = false)
    @NotEmpty
    @NotNull
    @Size(min = 8, max = 255, message = "The length for the password must be between 8 and 255 characters")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "enabled_by_admin", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    private Boolean enabledByAdmin;

    @Column(name = "verified_email", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    private Boolean verified_email;

    @Column(name = "description", nullable = false)
    @NotEmpty(message = "Description is required")
    @NotNull
    @Size(min = 10, max = 255, message = "The length must be between 10 and 255 characters")
    private String description;

    @Column(name = "role", nullable = false)
    @NotEmpty
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String role = "ROLE_DEV"; // default value

    @Column(name = "creationDate", nullable = false)
    private LocalDate creationDate = LocalDate.now(); // default value

    @Column(name = "blocking_reason")
    private String blockingReason;

    @OneToOne
    private ActivationCode activationCode; // one-to-one relationship with ActivationCode needed for activation

    @OneToOne
    private DeactivationCode deactivationCode; // one-to-one relationship with DeactivationCode needed for deactivation

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    public API_User() { // default constructor

    }
}
