package com.example.demo.JPA;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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

    public APIAccess() {
    }

}