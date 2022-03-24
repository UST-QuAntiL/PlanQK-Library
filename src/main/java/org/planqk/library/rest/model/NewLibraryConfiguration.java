package org.planqk.library.rest.model;

import com.fasterxml.jackson.annotation.JacksonAnnotation;

public class NewLibraryConfiguration {
    public String libraryName;

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }
}
