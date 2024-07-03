package de.olech2412.mensahub.junction.config;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

/**
 * This class is used to load the configuration file, encrypt and decrypt properties
 *
 * @author olech2412
 * @since 1.0.0
 */
@Slf4j
public class Config {

    private static final String CONFIG_FILE_PATH = System.getProperty("user.home") + "/mensaHub/mensaHub.settings";
    private static final long RELOAD_INTERVAL; // 1 Stunde in Millisekunden
    private static Config instance;

    // Load reload interval from config file
    static {
        try {
            RELOAD_INTERVAL = Long.parseLong(Config.getInstance().getProperty("mensaHub.dataDispatcher.config.reloadInterval"));
        } catch (IOException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException |
                 NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private final String ENCRYPTION_KEY = System.getenv("encryption.key");
    private Properties properties;
    private long lastAccessTime;
    private Cipher encryptionCipher;
    private Cipher decryptionCipher;

    /**
     * Creates a new instance of the config
     *
     * @throws IOException               If the file could not be read
     * @throws NoSuchPaddingException    If the padding is invalid
     * @throws NoSuchAlgorithmException  If the algorithm is invalid
     * @throws InvalidKeyException       If the key is invalid
     * @throws IllegalBlockSizeException If the block size is invalid
     * @throws BadPaddingException       If the padding is invalid
     */
    private Config() throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        loadProperties();
        initialEncryption();
        updateLastAccessTime();
    }

    /**
     * Returns the instance of the config
     *
     * @return The instance of the config
     * @throws IOException               If the file could not be read
     * @throws NoSuchPaddingException    If the padding is invalid
     * @throws NoSuchAlgorithmException  If the algorithm is invalid
     * @throws InvalidKeyException       If the key is invalid
     * @throws IllegalBlockSizeException If the block size is invalid
     * @throws BadPaddingException       If the padding is invalid
     */
    public static Config getInstance() throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        if (instance == null || instance.shouldReloadProperties()) {
            instance = new Config();
        }
        return instance;
    }

    /**
     * Initializes the encryption and decryption ciphers
     * and encrypts the properties that should be encrypted
     *
     * @throws NoSuchPaddingException    If the padding is invalid
     * @throws NoSuchAlgorithmException  If the algorithm is invalid
     * @throws InvalidKeyException       If the key is invalid
     * @throws IllegalBlockSizeException If the block size is invalid
     * @throws BadPaddingException       If the padding is invalid
     * @throws IOException               If the file could not be read
     */
    private void initialEncryption() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, IOException {
        // Initialize encryption and decryption ciphers
        SecretKeySpec keySpec = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), "AES");
        encryptionCipher = Cipher.getInstance("AES");
        encryptionCipher.init(Cipher.ENCRYPT_MODE, keySpec);
        decryptionCipher = Cipher.getInstance("AES");
        decryptionCipher.init(Cipher.DECRYPT_MODE, keySpec);
        List<String> propertiesToEncrypt = new ArrayList<>();
        propertiesToEncrypt.add("mensaHub.database.location");
        propertiesToEncrypt.add("mensaHub.database.password");
        propertiesToEncrypt.add("mensaHub.database.name");
        propertiesToEncrypt.add("mensaHub.database.username");
        propertiesToEncrypt.add("mensaHub.junction.mail.sender.password");

        for (String key : propertiesToEncrypt) {
            String property = getPropertyEncrypted(key);
            if (!property.startsWith("{")) {
                encryptProperty(key, property);
                log.info("Encrypted property {}", key);
            } else {
                log.info("Property {} already encrypted", key);
            }
        }
    }

    /**
     * Loads the properties file
     *
     * @throws IOException              If the file could not be read
     * @throws NoSuchPaddingException   If the padding is invalid
     * @throws NoSuchAlgorithmException If the algorithm is invalid
     * @throws InvalidKeyException      If the key is invalid
     */
    private void loadProperties() throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        properties = new Properties();
        properties.load(new FileInputStream(CONFIG_FILE_PATH));
    }

    /**
     * Updates the last access time
     */
    private void updateLastAccessTime() {
        lastAccessTime = System.currentTimeMillis();
    }

    /**
     * Checks if the properties file should be reloaded
     * this is the case if the last access time is longer ago than the reload interval
     *
     * @return True if the properties file should be reloaded, false otherwise
     */
    private boolean shouldReloadProperties() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastAccessTime) > RELOAD_INTERVAL;
    }

    /**
     * Returns the property value
     * if the property value is encrypted, it will be decrypted
     *
     * @param key The key of the property
     * @return The property value
     * @throws IllegalBlockSizeException If the block size is invalid
     * @throws BadPaddingException       If the padding is invalid
     * @throws NoSuchPaddingException    If the padding is invalid
     * @throws IOException               If the file could not be read
     * @throws NoSuchAlgorithmException  If the algorithm is invalid
     * @throws InvalidKeyException       If the key is invalid
     */
    public String getProperty(String key) throws IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        if (shouldReloadProperties()) {
            try {
                loadProperties();
                updateLastAccessTime();
            } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
                log.error("Error while loading properties", e);
            }
        }

        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Key " + key + " not found in configuration file");
        }

        // Check if property value is encrypted
        if (value.startsWith("{")) {
            // Decrypt the encrypted property value
            value = value.replaceAll("\\{", "");
            value = value.replaceAll("}", "");
            byte[] decryptedValue = decryptionCipher.doFinal(Base64.getDecoder().decode(value));
            value = new String(decryptedValue);
        }

        updateLastAccessTime();
        return value;
    }

    /**
     * Returns the property value without decrypting it
     * Only use this method if you are sure that the property is encrypted
     *
     * @param key The key of the property
     * @return The encrypted property value
     */
    private String getPropertyEncrypted(String key) {
        if (shouldReloadProperties()) {
            try {
                loadProperties();
                updateLastAccessTime();
            } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
                log.error("Error while loading properties", e);
            }
        }

        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Key " + key + " not found in configuration file");
        }

        updateLastAccessTime();
        return value;
    }

    /**
     * Encrypts the property value and saves it to the properties file
     *
     * @param key   The key of the property
     * @param value The value of the property
     * @throws IOException               If the file could not be read
     * @throws IllegalBlockSizeException If the block size is invalid
     * @throws BadPaddingException       If the padding is invalid
     * @throws NoSuchPaddingException    If the padding is invalid
     * @throws NoSuchAlgorithmException  If the algorithm is invalid
     * @throws InvalidKeyException       If the key is invalid
     */
    public void encryptProperty(String key, String value) throws IOException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        if (properties == null) {
            loadProperties();
        }

        // Encrypt the value
        byte[] encryptedValue = encryptionCipher.doFinal(value.getBytes());
        value = "{" + Base64.getEncoder().encodeToString(encryptedValue) + "}";

        properties.setProperty(key, value);
        updateProperties();
    }

    /**
     * Updates the properties file
     *
     * @throws IOException If the file could not be read
     */
    private void updateProperties() throws IOException {
        FileOutputStream out = new FileOutputStream(CONFIG_FILE_PATH);
        properties.store(out, "Updated from MensaHub-Junction");
        log.info("Updated properties");
        log.warn("It is recommended to restart other MensaHub services to apply the changes to them");
        out.close();
    }
}