package de.olech2412.mensahub.models.jobs;

import de.olech2412.mensahub.models.authentification.MailUser;
import de.olech2412.mensahub.models.authentification.Users;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Enumerated(EnumType.STRING)
    private JobType jobType;

    @ManyToMany
    private List<MailUser> mailUsers;

    @ManyToOne
    private Users creator;

    @ManyToOne
    private Users proponent;

    @CreationTimestamp
    @EqualsAndHashCode.Exclude
    private LocalDateTime creationDate;

    private LocalDateTime executeAt;

    private boolean executed;

    private boolean enabled;

    private boolean needsPermission;

    @Enumerated(EnumType.STRING)
    private JobStatus jobStatus;

    private String title; // used for the push notifications

    private String message; // used for the push notifications

    @Override
    public String toString() {
        return "JobDTO{" +
                "uuid=" + uuid +
                ", job=" + jobType +
                ", mailUsers=" + mailUsers +
                ", creator=" + creator +
                ", proponent=" + proponent +
                ", creationDate=" + creationDate +
                ", executeAt=" + executeAt +
                ", executed=" + executed +
                ", enabled=" + enabled +
                ", needsPermission=" + needsPermission +
                ", jobStatus=" + jobStatus +
                '}';
    }
}