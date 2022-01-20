package properties;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyService {
    private static PropertyService instance;
    private final Properties serverProperties;

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyService.class);

    private PropertyService() {
        serverProperties = loadProperties();
    }

    private static Properties loadProperties() {
        try (InputStream stream = PropertyService.class.getClassLoader().getResourceAsStream("config.properties")){
            Properties properties = new Properties();
            properties.load(stream);
            return properties;
        } catch (IOException e) {
            LOGGER.error("Cannot load configuration file.");
            throw new RuntimeException("Cannot load properties file.");
        }
    }

    public String getServerUri() {
        return serverProperties.getProperty("serverUri");
    }

    public Path getWorkingDirectory() {
        return Paths.get(serverProperties.getProperty("workingDirectory"));
    }

    public static PropertyService getInstance() {
        if (instance == null) {
            instance = new PropertyService();
        }
        return instance;
    }
}
