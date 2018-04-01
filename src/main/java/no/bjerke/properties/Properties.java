package no.bjerke.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Utility to handle environment variables and system properties
 */
@SuppressWarnings("WeakerAccess")
public class Properties {

    private static final Logger LOGGER = LoggerFactory.getLogger(Properties.class);
    private static final Pattern KEY_PATTERN = Pattern.compile("^[A-Z_]+[A-Z0-9_]*$");

    /**
     * Finds a environment variable or system property value given a key, environment variable takes
     * precedence over a system property if both exists
     *
     * @param key The environment variable or system property key to find a value for
     * @return the value of the environment variable or system property found for the given key
     * @throws IllegalArgumentException if the provided key is not a valid key given the POSIX standard
     */
    public static Optional<String> getProperty(String key) {
        LOGGER.debug("Fetching property with key: {}", key);
        validate(key);
        final var property = System.getProperty(key);
        if(property != null) {
            LOGGER.info("Found system property for key {} with value {}", key, property);
            return Optional.of(property);
        }
        final var envValue = System.getenv(key);
        if(envValue != null) {
            LOGGER.info("Found environment variable for key {} with value {}", key, envValue);
            return Optional.of(envValue);
        }
        LOGGER.info("Unable to find environment variable or system property for key {}", key);
        return Optional.empty();
    }

    private static void validate(String key) {
        Objects.requireNonNull(key, "Key may not be null");
        if(key.contains(".")) {
            throw new IllegalArgumentException("Property key may not contain dots");
        }
        if(Character.isDigit(key.charAt(0))) {
            throw new IllegalArgumentException("Property key may not start with a digit");
        }
        if(!KEY_PATTERN.matcher(key).find()) {
            throw new IllegalArgumentException("Illegal key for environment variable/system property: " + key);
        }
    }

}