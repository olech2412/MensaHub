package de.olech2412.mensahub.junction.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

    private static final String CONFIG_FILE_PATH = System.getProperty("user.home") + "/mensaHub/mensaHub.settings";
    private static final long RELOAD_INTERVAL; // 1 Stunde in Millisekunden

    static {
        try {
            RELOAD_INTERVAL = Long.parseLong(Config.getInstance().getProperty("mensaHub.junction.config.reloadInterval"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Config instance;
    private Properties properties;
    private long lastAccessTime;

    private Config() throws IOException {
        loadProperties();
        updateLastAccessTime();
    }

    public static Config getInstance() throws IOException {
        if (instance == null || instance.shouldReloadProperties()) {
            instance = new Config();
        }
        return instance;
    }

    private void loadProperties() throws IOException {
        properties = new Properties();
        properties.load(new FileInputStream(CONFIG_FILE_PATH));
    }

    private void updateLastAccessTime() {
        lastAccessTime = System.currentTimeMillis();
    }

    private boolean shouldReloadProperties() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastAccessTime) > RELOAD_INTERVAL;
    }

    public String getProperty(String key) {
        if (shouldReloadProperties()) {
            try {
                loadProperties();
                updateLastAccessTime();
            } catch (IOException e) {
                e.printStackTrace();
                // Handle exception (e.g., log, throw, etc.)
            }
        }

        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Key " + key + " not found in configuration file");
        }

        updateLastAccessTime();
        return value;
    }
}
