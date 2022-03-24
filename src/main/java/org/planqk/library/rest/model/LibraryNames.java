package org.planqk.library.rest.model;

import java.util.List;

import org.glassfish.jersey.internal.guava.Lists;

public class LibraryNames {
    public List<String> libraryNames;

    public LibraryNames(List<String> names) {
        libraryNames = names;
    }
}
