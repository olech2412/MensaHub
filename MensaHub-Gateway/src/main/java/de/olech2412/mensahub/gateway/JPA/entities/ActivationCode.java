package de.olech2412.mensahub.gateway.JPA.entities;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Entity
@Table(name = "activation_codes")
/**
 * Used to create an ActivationCode
 */
public class ActivationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private String code;

    public ActivationCode(String code) {
        this.code = code;
    }

    public ActivationCode() {

    }


}
