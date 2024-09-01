package de.olech2412.mensahub.datadispatcher.data.leipzig.html_caller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;

@Component
@EnableScheduling
@Slf4j
public class HashStore {

    private final ConcurrentHashMap<LocalDate, Integer> hashMap = new ConcurrentHashMap<>();

    public boolean isSameHash(LocalDate date, int newHash) {
        Integer existingHash = hashMap.get(date);
        return existingHash != null && existingHash == newHash;
    }

    public void saveHash(LocalDate date, int hash) {
        hashMap.put(date, hash);
    }

    /**
     * Every day at 4:56 am drop the saved hash values
     */
    @Scheduled(cron = "0 56 4 * * *")
    public void dropSavedHashValues(){
        log.info("Dropped saved hash values: {}", hashMap.size());
        hashMap.clear();
    }
}
