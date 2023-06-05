package com.essensGetter.api.JPA.entities.authentication;

import com.essensGetter.api.JPA.entities.ActivationCode;
import com.essensGetter.api.JPA.entities.DeactivationCode;
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
public class API_User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "api_user_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Long apiUserId;

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
    private String role = "ROLE_DEV";

    @Column(name = "creationDate", nullable = false)
    private LocalDate creationDate = LocalDate.now();

    @Column(name = "blocking_reason")
    private String blockingReason;

    @OneToOne
    private ActivationCode activationCode;

    @OneToOne
    private DeactivationCode deactivationCode;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    public API_User() {

    }
}
