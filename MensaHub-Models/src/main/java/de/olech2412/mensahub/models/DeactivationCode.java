package de.olech2412.mensahub.models;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "deactivation_codes")
public class DeactivationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private String code;

    public DeactivationCode(String code) {
        this.code = code;
    }

    public DeactivationCode() {

    }

}
