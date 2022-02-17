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

    private Properties loadProperties() {
        try (InputStream stream = ServerPropertyService.class.getClassLoader().getResourceAsStream("config.properties")){
            Properties properties = new Properties();
            properties.load(stream);
            if(properties.getProperty("workingDirectory") == null || properties.getProperty("workingDirectory").isBlank()) {
                properties.setProperty("workingDirectory", System.getProperty("user.home") + "/planqk-library");
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
