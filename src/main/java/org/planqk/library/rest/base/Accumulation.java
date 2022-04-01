package org.planqk.library.rest.base;

import java.io.IOException;
import java.util.List;

import org.jabref.model.entry.BibEntry;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.planqk.library.core.properties.ServerPropertyService;
import org.planqk.library.core.repository.LibraryService;
import org.planqk.library.rest.model.BibEntries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("all")
@Tag(name = "All Libraries")
public class Accumulation {
    private static final Logger LOGGER = LoggerFactory.getLogger(Accumulation.class);
    private final LibraryService libraryService;

    public Accumulation() {
        libraryService = LibraryService.getInstance(ServerPropertyService.getInstance().getWorkingDirectory());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEntries() throws IOException {
        try {
            List<BibEntry> entries = libraryService.getAllEntries();
            return Response.ok(new BibEntries(entries))
                           .build();
        } catch (IOException e) {
            LOGGER.error("Error accumulating all entries.", e);
            throw e;
        }
    }
}
