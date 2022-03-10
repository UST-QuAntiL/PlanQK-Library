package org.planqk.library.rest.base;

import java.io.IOException;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.jabref.model.entry.BibEntry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.planqk.library.core.properties.ServerPropertyService;
import org.planqk.library.core.repository.LibraryService;
import org.planqk.library.core.serialization.BibEntryAdapter;
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
    public Response getAllEntries() {
        try {
            List<BibEntry> entries = libraryService.getAllEntries();
            Gson gson = new GsonBuilder().registerTypeAdapter(BibEntry.class, new BibEntryAdapter()).create();
            String json = gson.toJson(entries);
            return Response.ok()
                           .entity(json)
                           .build();
        } catch (IOException e) {
            return Response.serverError()
                           .entity(e.getMessage())
                           .build();
        }
    }
}
