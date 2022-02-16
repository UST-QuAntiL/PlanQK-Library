package rest.base;

import java.io.IOException;
import java.util.List;

import org.jabref.model.entry.BibEntry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import logic.properties.ServerPropertyService;
import logic.repository.LibraryService;
import logic.serialization.BibEntryAdapter;

public class Accumulation {
    private static final Logger LOGGER = LoggerFactory.getLogger(Accumulation.class);
    private final LibraryService libraryService;

    public Accumulation() {
        LOGGER.info("Create resource....");
        libraryService = LibraryService.getInstance(ServerPropertyService.getInstance().getWorkingDirectory());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEntries() {
        LOGGER.info("getting all entries...");
        try {
            List<BibEntry> entries = libraryService.getAllEntries();
            Gson gson = new GsonBuilder().registerTypeAdapter(BibEntry.class, new BibEntryAdapter()).create();
            String json = gson.toJson(entries);
            return Response.ok()
                           .entity(json)
                           .build();
        } catch (IOException e) {
            LOGGER.info("getting all entries failed");
            return Response.serverError()
                           .entity(e.getMessage())
                           .build();
        }
    }
}
