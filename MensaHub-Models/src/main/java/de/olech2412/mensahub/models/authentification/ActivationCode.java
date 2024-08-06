package de.olech2412.mensahub.models.authentification;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents the activation code for a user.
 *
 * @author olech2412
 * @since 1.0.0
 */
@Setter
@Getter
@Entity
@Table(name = "activation_codes")
public class ActivationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private String code; // the code is a random string

    /**
     * Constructor for the activation code.
     *
     * @param code the code
     */
    public ActivationCode(String code) {
        this.code = code;
    }

    /**
     * Default constructor for the activation code.
     */
    public ActivationCode() {

    }
}