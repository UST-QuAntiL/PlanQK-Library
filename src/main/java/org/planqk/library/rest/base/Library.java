package org.planqk.library.rest.base;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jabref.model.entry.BibEntry;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.planqk.library.core.properties.ServerPropertyService;
import org.planqk.library.core.repository.LibraryService;
import org.planqk.library.core.representation.CSLStyleAdapter;
import org.planqk.library.core.serialization.BibEntryMapper;
import org.planqk.library.rest.model.BibEntryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Library {
    private static final Logger LOGGER = LoggerFactory.getLogger(Library.class);
    private final LibraryService libraryService;
    private final String libraryName;

    public Library(String libraryName) {
        this.libraryName = libraryName;
        libraryService = LibraryService.getInstance(ServerPropertyService.getInstance().getWorkingDirectory());
    }

    public Library(java.nio.file.Path directory, String libraryName) {
        this.libraryName = libraryName;
        libraryService = LibraryService.getInstance(directory);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public org.planqk.library.rest.model.Library getLibraryEntries() throws IOException {
        try {
            List<BibEntry> entries = libraryService.getLibraryEntries(libraryName);
            return new org.planqk.library.rest.model.Library(entries.parallelStream().map(BibEntryMapper::map).collect(Collectors.toList()));
        } catch (IOException e) {
            LOGGER.error("Error retrieving entries.", e);
            throw e;
        }
    }

    @GET
    @Path("styles")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getCSLStyles() throws IOException, URISyntaxException {
        return CSLStyleAdapter.getInstance().getRegisteredStyles();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void addEntryToLibrary(BibEntryDTO bibEntry) throws IOException {
        try {
            libraryService.addEntryToLibrary(libraryName, BibEntryMapper.map(bibEntry));
        } catch (IOException e) {
            LOGGER.error("Error adding entry.", e);
            throw e;
        }
    }

    @DELETE
    public Response deleteLibrary() throws IOException {
        try {
            if (libraryService.deleteLibrary(libraryName)) {
                return Response.ok().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                               .build();
            }
        } catch (IOException e) {
            LOGGER.error("Error deleting library.", e);
            throw e;
        }
    }

    @GET
    @Path("{citeKey}")
    @Produces(MediaType.APPLICATION_JSON)
    public BibEntryDTO getBibEntryMatchingCiteKey(@PathParam("citeKey") String citeKey) throws IOException {
        try {
            Optional<BibEntry> entry = libraryService.getLibraryEntryMatchingCiteKey(libraryName, citeKey);
            if (entry.isPresent()) {
                return BibEntryMapper.map(entry.get());
            } else {
                throw new NotFoundException();
            }
        } catch (IOException e) {
            LOGGER.error("Error finding matching entry.", e);
            throw e;
        }
    }

    @GET
    @Path("{citeKey}/{cslStyle}")
    @Produces(MediaType.TEXT_HTML)
    public String getBibEntryMatchingCiteKey(@PathParam("citeKey") String citeKey, @PathParam("cslStyle") String cslStyle) throws IOException, URISyntaxException {
        try {
            Optional<BibEntry> entry = libraryService.getLibraryEntryMatchingCiteKey(libraryName, citeKey);
            if (entry.isPresent()) {
                return CSLStyleAdapter.getInstance().generateCitation(entry.get(), cslStyle);
            } else {
                throw new NotFoundException();
            }
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("Error finding matching entry or generating the styled entry.", e);
            throw e;
        }
    }

    @PUT
    @Path("{citeKey}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateEntry(@PathParam("citeKey") String citeKey, BibEntryDTO bibEntry) throws IOException {
        try {
            BibEntry updatedEntry = BibEntryMapper.map(bibEntry);
            libraryService.updateEntry(libraryName, citeKey, updatedEntry);
        } catch (IOException e) {
            LOGGER.error("Error updating entry.", e);
            throw e;
        }
    }

    @DELETE
    @Path("{citeKey}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteEntryFromLibrary(@PathParam("citeKey") String citeKey) throws IOException {
        try {
            boolean foundAndDeleted = libraryService.deleteEntryByCiteKey(libraryName, citeKey);
            if (foundAndDeleted) {
                return Response.ok()
                               .build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                               .build();
            }
        } catch (IOException e) {
            LOGGER.error("Error deleting entry.", e);
            throw e;
        }
    }
}
