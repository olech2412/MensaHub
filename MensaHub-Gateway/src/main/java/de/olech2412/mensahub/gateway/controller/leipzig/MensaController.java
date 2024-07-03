package de.olech2412.mensahub.gateway.controller.leipzig;

import de.olech2412.mensahub.gateway.jpa.services.mensen.MensaService;
import de.olech2412.mensahub.models.Mensa;
import io.micrometer.core.annotation.Timed;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Log4j2
@RequestMapping("/mensa")
@CrossOrigin(origins = "*")
@Timed
public class MensaController {

    private final MensaService mensaService;

    /**
     * Constructor for MealController
     *
     * @param mensaService Service for the cafeteria
     */
    public MensaController(MensaService mensaService) {
        this.mensaService = mensaService;
    }

    @GetMapping("/getMensas")
    public List<Mensa> getMensa() {
        log.info("Mensa info requested");
        return mensaService.findAll();
    }

    @GetMapping("/getMensaByName")
    public Mensa getMensaByName(String name) {
        log.info("Mensa info requested for {}", name);
        return mensaService.getMensaByName(name);
    }

    @GetMapping("/getMensaById")
    public Mensa getMensaById(long id) {
        log.info("Mensa info requested for {}", id);
        return mensaService.getMensa(id);
    }

}
