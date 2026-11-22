package com.prasandeep.api.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

/** Loads API framework config from classpath, then env / system overrides. */
public final class ConfigReader {

    private static final Properties PROPERTIES = new Properties();

    static {
        load("config.properties");
        loadOptional("config.local.properties");
    }

    private ConfigReader() {
    }

    private static void load(String name) {
        try (InputStream in = ConfigReader.class.getClassLoader().getResourceAsStream(name)) {
            if (in == null) {
                throw new IllegalStateException("Missing resource: " + name);
            }
            PROPERTIES.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + name, e);
        }
    }

    private static void loadOptional(String name) {
        try (InputStream in = ConfigReader.class.getClassLoader().getResourceAsStream(name)) {
            if (in != null) {
                PROPERTIES.load(in);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load optional " + name, e);
        }
    }

    public static String get(String key) {
        return resolve(key).orElseThrow(() -> new IllegalStateException("Missing config: " + key));
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    private static Optional<String> resolve(String key) {
        String env = System.getenv(key.toUpperCase().replace('.', '_'));
        if (env != null && !env.isBlank()) {
            return Optional.of(env.trim());
        }
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) {
            return Optional.of(sys.trim());
        }
        String file = PROPERTIES.getProperty(key);
        return file == null ? Optional.empty() : Optional.of(file.trim());
    }
}
