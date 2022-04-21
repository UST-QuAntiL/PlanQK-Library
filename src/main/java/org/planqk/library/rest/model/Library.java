package org.planqk.library.rest.model;

import java.util.List;

import org.jabref.model.entry.BibEntry;

public class Library {
    public List<BibEntryDTO> bibEntries;

    public Library(List<BibEntryDTO> bibEntries) {
        this.bibEntries = bibEntries;
    }
}
