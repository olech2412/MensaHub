package de.olech2412.mensahub.models.jobs;

import de.olech2412.mensahub.models.authentification.MailUser;
import de.olech2412.mensahub.models.authentification.Users;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class JobDTO {

    private Job job;

    private List<MailUser> users;

    @Override
    public String toString() {
        return "JobDTO{" +
                "job=" + job +
                ", users=" + users +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobDTO jobDTO = (JobDTO) o;
        return job == jobDTO.job && Objects.equals(users, jobDTO.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(job, users);
    }
}
