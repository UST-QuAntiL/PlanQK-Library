import java.net.URI;

import properties.PropertyService;
import jakarta.ws.rs.core.UriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class JabRefServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(JabRefServer.class);

    public static void main(String[] args) {
        String serverUri = PropertyService.getInstance().getServerUri();

        URI baseUri = UriBuilder.fromUri(serverUri).build();
        ResourceConfig config = ResourceConfig.forApplicationClass(JabRefAPI.class);

        // TODO: Change deployment to tomcat
        JdkHttpServerFactory.createHttpServer(baseUri, config);
        LOGGER.info("Http Server running at {}", serverUri);
    }
}
