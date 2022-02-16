import java.net.URI;

import logic.properties.ServerPropertyService;
import jakarta.ws.rs.core.UriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import rest.JabRefAPI;

public class JabRefServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(JabRefServer.class);

    public static void main(String[] args) {
        String serverUri = ServerPropertyService.getInstance().getServerUri();

        URI baseUri = UriBuilder.fromUri(serverUri).build();
        ResourceConfig config = ResourceConfig.forApplicationClass(JabRefAPI.class);

        // TODO: Change deployment to tomcat
        JdkHttpServerFactory.createHttpServer(baseUri, config);
        LOGGER.info("Http Server running at {}", serverUri);
    }
}
