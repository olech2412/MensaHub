package de.olech2412.mensahub.models.result.errors;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "errors")
public class ErrorEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Lob
    private String message;

    private String code;

    @Enumerated(EnumType.STRING)
    private Application application;

    @CreationTimestamp
    private LocalDateTime creationTime;

    public ErrorEntity(String message, String code, Application application) {
        this.message = message;
        this.code = code;
        this.application = application;
    }

    public ErrorEntity() {

    }
}
