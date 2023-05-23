package com.essensGetter.api.JPA.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_access")
@Getter
@Setter
public class APIAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    private String token;

    public String description;
    public Long calls;

    public String email;

    public Boolean enabled;

    public Long maxCalls;

    public Boolean canRead;

    public Boolean canWrite;

    public LocalDateTime lastCall;

    public APIAccess() {
    }

}