package de.olech2412.mensahub.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

@Getter
@Setter
@MappedSuperclass
public class Mensa {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Exclude
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;

    private String apiUrl;

    public Mensa() {

    }

    public Mensa(Long id, String name, String apiUrl) {
        this.id = id;
        this.name = name;
        this.apiUrl = apiUrl;
    }
}