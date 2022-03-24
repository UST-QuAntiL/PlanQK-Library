package org.planqk.library.core.properties;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerPropertyService {
    private static ServerPropertyService instance;
    private final Properties serverProperties;

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerPropertyService.class);

    private ServerPropertyService() {
        serverProperties = loadProperties();
    }

    /**
     * Tries to determine the working directory of the library.
     * Uses the first path it finds when resolving in this order:
     *  1. config.properties
     *  2. Environment variable LIBRARY_WORKSPACE
     *  3. Default User home with a new directory for the library
     */
    private Properties loadProperties() {
        try (InputStream stream = ServerPropertyService.class.getClassLoader().getResourceAsStream("config.properties")){
            Properties properties = new Properties();
            properties.load(stream);
            if(properties.getProperty("workingDirectory") == null || properties.getProperty("workingDirectory").isBlank()) {
                if (!(System.getenv("LIBRARY_WORKSPACE") == null || System.getenv("LIBRARY_WORKSPACE").isBlank())) {
                    LOGGER.info("Environment Variable found, using defined directory: {}", System.getenv("LIBRARY_WORKSPACE"));
                    properties.setProperty("workingDirectory", System.getenv("LIBRARY_WORKSPACE"));
                } else {
                    LOGGER.info("Working directory was not found in either the properties or the environment variables, falling back to default location: {}", System.getProperty("user.home") + "/planqk-library");
                    properties.setProperty("workingDirectory", System.getProperty("user.home") + "/planqk-library");
                }
            }
            return properties;
        } catch (IOException e) {
            LOGGER.error("Cannot load configuration file.");
            throw new RuntimeException("Cannot load properties file.");
        }
    }

    public Path getWorkingDirectory() {
        return Paths.get(serverProperties.getProperty("workingDirectory"));
    }

    public static ServerPropertyService getInstance() {
        if (instance == null) {
            instance = new ServerPropertyService();
        }
        return instance;
    }
}
