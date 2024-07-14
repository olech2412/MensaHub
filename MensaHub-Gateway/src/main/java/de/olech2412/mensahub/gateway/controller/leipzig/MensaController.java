package de.olech2412.mensahub.gateway.controller.leipzig;

import de.olech2412.mensahub.gateway.jpa.services.mensen.MensaService;
import de.olech2412.mensahub.models.Mensa;
import io.micrometer.core.annotation.Timed;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
    public ResponseEntity<List<Mensa>> getMensa() {
        List<Mensa> mensas = mensaService.findAll();
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
                .body(mensas);
    }

    @GetMapping("/getMensaByName")
    public ResponseEntity<Mensa> getMensaByName(String name) {
        Mensa mensa = mensaService.getMensaByName(name);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
                .body(mensa);
    }

    @GetMapping("/getMensaById")
    public ResponseEntity<Mensa> getMensaById(long id) {
        Mensa mensa = mensaService.getMensa(id);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS))
                .body(mensa);
    }

}
