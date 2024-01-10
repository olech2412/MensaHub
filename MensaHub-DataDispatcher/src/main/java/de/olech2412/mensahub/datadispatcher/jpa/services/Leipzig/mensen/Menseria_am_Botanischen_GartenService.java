package de.olech2412.mensahub.datadispatcher.jpa.services.Leipzig.mensen;

import de.olech2412.mensahub.datadispatcher.jpa.repository.Leipzig.mensen.Menseria_am_Botanischen_GartenRepository;
import de.olech2412.mensahub.models.Leipzig.mensen.Menseria_am_Botanischen_Garten;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Menseria_am_Botanischen_GartenService extends Mensa_Service {

    @Autowired
    Menseria_am_Botanischen_GartenRepository menseria_am_botanischen_gartenRepository;

    /**
     * @return Menseria am Botanischen Garten as Iterable
     */
    @Override
    public Iterable<Menseria_am_Botanischen_Garten> findAll() {
        return menseria_am_botanischen_gartenRepository.findAll();
    }

    /**
     * @return Menseria am Botanischen Garten
     */
    @Override
    public Menseria_am_Botanischen_Garten getMensa() {
        List<Menseria_am_Botanischen_Garten> menseria_am_botanischen_gartenList = (List<Menseria_am_Botanischen_Garten>) menseria_am_botanischen_gartenRepository.findAll();
        return menseria_am_botanischen_gartenList.get(0);
    }
}

