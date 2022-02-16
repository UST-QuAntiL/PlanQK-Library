package rest;

import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.core.Application;
import rest.Root;
import rest.base.Accumulation;
import rest.base.Libraries;

public class JabRefAPI extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();

        // Rest root resources
        resources.add(Root.class);

        return resources;
    }
}
