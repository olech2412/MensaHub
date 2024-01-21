package de.olech2412.mensahub.datadispatcher.jpa.repository.Leipzig;

import de.olech2412.mensahub.models.Leipzig.Allergene;
import org.springframework.data.repository.ListCrudRepository;

public interface AllergeneRepository extends ListCrudRepository<Allergene, Long> {
}