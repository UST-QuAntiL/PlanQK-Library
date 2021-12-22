package properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyService.class);
    private static final Properties serverProperties = loadProperties();

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

    public static String getServerUri() {
        return serverProperties.getProperty("serverUri");
    }

    public static String getWorkingDirectory() {
        return serverProperties.getProperty("workingDirectory");
    }
}
