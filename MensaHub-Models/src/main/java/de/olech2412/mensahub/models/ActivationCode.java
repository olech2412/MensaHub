package de.olech2412.mensahub.models;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;


@Setter
@Getter
@Entity
@Table(name = "activation_codes")
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


}
