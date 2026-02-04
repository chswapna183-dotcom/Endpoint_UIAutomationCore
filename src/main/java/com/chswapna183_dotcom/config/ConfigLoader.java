package com.chswapna183_dotcom.config;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Objects;
import java.util.Properties;

public final class ConfigLoader {
    private static final String CONFIG_FILE = "config.properties";
    private static final Properties PROPERTIES = loadProperties();

    private ConfigLoader() {
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IllegalStateException("Missing " + CONFIG_FILE + " on the classpath");
            }
            props.load(input);
            return props;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + CONFIG_FILE, e);
        }
    }

    public static String get(String key) {
        String value = readValue(Objects.requireNonNull(key, "key"));
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException("Missing required config key: " + key);
        }
        return value;
    }

    public static String getOrDefault(String key, String defaultValue) {
        String value = readValue(Objects.requireNonNull(key, "key"));
        return (value == null || value.isEmpty()) ? defaultValue : value;
    }

    private static String readValue(String key) {
        String override = System.getProperty(key);
        if (override != null) {
            String trimmed = override.trim();
            if (!trimmed.isEmpty()) {
                return trimmed;
            }
        }

        String fromFile = PROPERTIES.getProperty(key);
        if (fromFile == null) {
            return null;
        }
        String trimmed = fromFile.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public static String getBaseUrl() {
        return get("baseUrl");
    }

    public static String getBrowser() {
        return getOrDefault("browser", "chrome");
    }

    public static Duration getTimeout() {
        String timeoutSeconds = getOrDefault("timeout", "10");
        try {
            return Duration.ofSeconds(Long.parseLong(timeoutSeconds));
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid timeout (seconds): " + timeoutSeconds, e);
        }
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(getOrDefault("headless", "true"));
    }

    public static String getApiBaseUrl() {
        return getOrDefault("apiBaseUrl", "https://reqres.in");
    }

    public static String getApiBasePath() {
        return getOrDefault("apiBasePath", "/api");
    }
}
