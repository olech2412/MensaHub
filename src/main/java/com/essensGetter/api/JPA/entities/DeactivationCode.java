package com.essensGetter.api.JPA.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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
