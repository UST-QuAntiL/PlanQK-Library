package rest;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.jabref.model.entry.BibEntry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import properties.PropertyService;
import repository.LibraryService;
import serialization.BibEntryAdapter;

@Path("Libraries")
public class Libraries {
    private final LibraryService libraryService;

    public Libraries() {
        libraryService = new LibraryService(Paths.get(PropertyService.getWorkingDirectory()));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLibraryNames() {
        try {
            return Response.ok(libraryService.getLibraryNames())
                           .build();
        } catch (IOException e) {
            return Response.serverError()
                           .entity(e.getMessage())
                           .build();
        }
    }

    @GET
    @Path("/{libraryName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLibraryEntries(@PathParam("libraryName") String libraryName) {
        if (!libraryService.(libraryName)) {
            return Response.status(Response.Status.NOT_FOUND).entity("Could not find library").build();
        }
        try {
            List<BibEntry> entries = libraryService.getLibraryEntries(libraryName);
            Gson gson = new GsonBuilder().registerTypeAdapter(BibEntry.class, BibEntryAdapter.class).create();
            return Response.ok(gson.toJson(entries))
                           .build();
        } catch (IOException e) {
            return Response.serverError()
                           .entity(e.getMessage())
                           .build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{libraryName}")
    public Response createNewLibrary(@PathParam("libraryName") String libraryName) {
        if (libraryService.libraryExists(libraryName)) {
            return Response.status(Response.Status.CONFLICT).entity("The given library name is already in use").build();
        }
        // TODO: Create BibDatabase at location and return a ok response if successful, otherwise 500
        return Response.serverError().entity("Not yet implemented").build();
    }

    @DELETE
    @Path("/{libraryName}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteLibrary(@PathParam("libraryName") String libraryName) {
        try {
            if (libraryService.deleteLibrary(libraryName)) {
                return Response.ok().build();
            } else {
                return Response.status(Response.Status.GONE)
                               .build();
            }
        } catch (IOException e) {
            return Response.serverError()
                           .entity(e.getMessage())
                           .build();
        }
    }

    @POST
    @Path("/{libraryName}")
    @Consumes(MediaType.APPLICATION_JSON)
    // Not sure, but we probably need a new DTO
    public Response createEntryInLibrary(@PathParam("libraryName") String libraryName,
                                         BibEntry entry) {
        return Response.serverError()
                       .entity("Not yet implemented")
                       .build();
        // TODO: Add the entry to specified library, if a different DTO is used, create a bib entry first.
    }
}
