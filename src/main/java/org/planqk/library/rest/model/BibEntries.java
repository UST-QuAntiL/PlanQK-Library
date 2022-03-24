package org.planqk.library.rest.model;

import java.util.List;

import org.jabref.model.entry.BibEntry;

public class BibEntries {
    public List<BibEntry> bibEntries;

    public BibEntries(List<BibEntry> bibEntries) {
        this.bibEntries = bibEntries;
    }
}
