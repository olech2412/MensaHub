package de.olech2412.mensahub.junction.jpa.repository;

import de.olech2412.mensahub.models.authentification.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionEntityRepository extends JpaRepository<SubscriptionEntity, Long> {

    SubscriptionEntity findByEndpoint(String endpointUrl);

}