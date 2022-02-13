package rest;

import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.core.Application;

public class JabRefAPI extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();

        // Rest endpoints
        resources.add(Root.class);
        resources.add(Libraries.class);
        resources.add(Accumulation.class);

        return resources;
    }
}
