package org.planqk.library.rest.model;

import org.jabref.model.entry.BibEntry;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class BibEntryDTO {
    @JsonIgnore
    public BibEntry entry;

    public BibEntryDTO(BibEntry entry) {
        this.entry = entry;
    }

    public BibEntryDTO(){

    }
}
