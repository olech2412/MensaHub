package de.mensahub.gateway.JPA.entities.mensen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@MappedSuperclass
/**
 * Used to provide a common base class for all Mensa entities
 */
public abstract class Mensa {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Exclude
    @Column(name = "id", nullable = false)
    @JsonIgnore
    private Long id;

    private String name;

    @JsonIgnore
    private String apiUrl;

    public Mensa() {

    }

    public Mensa(Long id, String name, String apiUrl) {
        this.id = id;
        this.name = name;
        this.apiUrl = apiUrl;
    }
}