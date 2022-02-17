package org.planqk.library.rest;

import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.core.Application;

public class PlanQKLibrary extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();

        // Rest root resources
        resources.add(Root.class);

        return resources;
    }
}
