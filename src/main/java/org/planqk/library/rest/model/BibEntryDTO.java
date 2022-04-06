package org.planqk.library.rest.model;

import org.jabref.model.entry.BibEntry;

import io.swagger.v3.oas.annotations.Hidden;

public class BibEntryDTO {
    @Hidden
    public BibEntry entry;

    public BibEntryDTO(BibEntry entry) {
        this.entry = entry;
    }

    public BibEntryDTO(){

    }
}
