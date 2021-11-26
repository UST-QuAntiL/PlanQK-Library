import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import rest.Libraries;
import rest.Root;

@ApplicationPath("/")
public class JabRefAPI extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();

        // Rest endpoints
        resources.add(Root.class);
        resources.add(Libraries.class);

        return resources;
    }
}
