package de.olech2412.mensahub.models.authentification;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents the deactivation code for a user.
 *
 * @author olech2412
 * @since 1.0.0
 */
@Setter
@Getter
@Entity
@Table(name = "deactivation_codes")
public class DeactivationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id; // primary key
    private String code; // the code is a random string

    /**
     * Constructor for the deactivation code.
     *
     * @param code the code
     */
    public DeactivationCode(String code) {
        this.code = code;
    }

    /**
     * Default constructor for the deactivation code.
     */
    public DeactivationCode() {

    }

}
