package de.olech2412.mensahub.models.jobs;

import de.olech2412.mensahub.models.authentification.MailUser;
import de.olech2412.mensahub.models.authentification.Users;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "jobs")
public class JobDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Enumerated(EnumType.STRING)
    private Job job;

    @OneToMany
    private List<MailUser> mailUsers;

    @OneToOne
    private Users creator;

    @ManyToOne
    private Users proponent;

    @CreationTimestamp
    private LocalDateTime creationDate;

    private LocalDateTime executeAt;

    private boolean transferredByInterface;

    private boolean executed;

    private boolean enabled;

    private boolean needsPermission;

    @Enumerated(EnumType.STRING)
    private JobStatus jobStatus;

    @Override
    public String toString() {
        return "JobDTO{" +
                "uuid=" + uuid +
                ", job=" + job +
                ", mailUsers=" + mailUsers +
                ", creator=" + creator +
                ", proponent=" + proponent +
                ", creationDate=" + creationDate +
                ", executeAt=" + executeAt +
                ", transferredByInterface=" + transferredByInterface +
                ", executed=" + executed +
                ", enabled=" + enabled +
                ", needsPermission=" + needsPermission +
                ", jobStatus=" + jobStatus +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobDTO jobDTO = (JobDTO) o;
        return transferredByInterface == jobDTO.transferredByInterface && executed == jobDTO.executed && enabled == jobDTO.enabled && needsPermission == jobDTO.needsPermission && job == jobDTO.job && Objects.equals(mailUsers, jobDTO.mailUsers) && Objects.equals(creator, jobDTO.creator) && Objects.equals(proponent, jobDTO.proponent) && Objects.equals(creationDate, jobDTO.creationDate) && Objects.equals(executeAt, jobDTO.executeAt) && jobStatus == jobDTO.jobStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(job, mailUsers, creator, proponent, creationDate, executeAt, transferredByInterface, executed, enabled, needsPermission, jobStatus);
    }
}