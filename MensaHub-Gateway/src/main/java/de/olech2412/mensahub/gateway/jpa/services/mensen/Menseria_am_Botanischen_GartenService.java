package de.olech2412.mensahub.gateway.jpa.services.mensen;

import de.olech2412.mensahub.gateway.jpa.repository.mensen.Menseria_am_Botanischen_GartenRepository;
import de.olech2412.mensahub.models.Leipzig.mensen.Menseria_am_Botanischen_Garten;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Menseria_am_Botanischen_GartenService extends Mensa_Service {

    @Autowired
    Menseria_am_Botanischen_GartenRepository menseria_am_botanischen_gartenRepository;

    /**
     * @return Menseria am Botanischen Garten as List
     */
    @Override
    public List<Menseria_am_Botanischen_Garten> findAll() {
        return menseria_am_botanischen_gartenRepository.findAll();
    }

    /**
     * @return Menseria am Botanischen Garten
     */
    @Override
    public Menseria_am_Botanischen_Garten getMensa() {
        return menseria_am_botanischen_gartenRepository.findMenseria_am_Botanischen_GartenById(1L); // There is only one Menseria am Botanischen Garten
    }
}

