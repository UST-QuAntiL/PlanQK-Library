package rest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.jabref.model.database.BibDatabase;
import org.jabref.model.entry.BibEntry;

import Properties.PropertyService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("Libraries")
public class Libraries {
    private static final Logger LOGGER = LoggerFactory.getLogger(Libraries.class);

    java.nio.file.Path workingDirectory;

    public Libraries() {
        this.workingDirectory = Paths.get(PropertyService.getWorkingDirectory());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLibraryNames() {
        try {
            List<String> libraries = Files.list(workingDirectory)                // Alternatively walk(Path start, int depth) for recursive aggregation
                                          .filter(file -> !Files.isDirectory(file))
                                          .map(java.nio.file.Path::getFileName)
                                          .map(java.nio.file.Path::toString)
                                          .filter(file -> file.endsWith(".bib"))
                                          .collect(Collectors.toList());
            return Response.ok(libraries)
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
        java.nio.file.Path pathToLibrary = findLibrary(libraryName);
        if (Files.exists(pathToLibrary)) {
            // TODO: Load database and get entries from it, if a separate DTO is used, convert entries into DTO instances
        }
        return Response.status(404).entity("Could not find library").build(); // Http 404 - Resouce not found
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{libraryName}")
    public Response createNewLibrary(@PathParam("libraryName") String libraryName) {
        java.nio.file.Path pathToLibrary = findLibrary(libraryName);
        if (Files.exists(pathToLibrary)) {
            // Http 409 - Conflict
            return Response.status(409).entity("The given library name is already in use").build();
        }
        // TODO: Create BibDatabase at location and return a ok response if successfull, otherwise 500
        return Response.serverError().entity("Not yet implemented").build();
    }

    @DELETE
    @Path("/{libraryName}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteLibrary(@PathParam("libraryName") String libraryName) {
        java.nio.file.Path pathToLibrary = findLibrary(libraryName);
        try {
            if (Files.deleteIfExists(pathToLibrary)) {
                return Response.ok().build();
            } else {
                // Http 410 - Gone TODO: Enums for codes
                return Response.status(410).build();
            }
        } catch (IOException e) {
            return Response.serverError().entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
        }
    }

    @POST
    @Path("/{libraryName}")
    @Consumes(MediaType.APPLICATION_JSON)
    // Not sure, but we probably need a new DTO
    public Response getLibraryNames(@PathParam("libraryName") String libraryName,
                                    BibEntry entry) {
        return Response.serverError()
                       .entity("Not yet implemented")
                       .build();
        // TODO: Add the entry to specified library, if a different DTO is used, create a bib entry first.
    }

    private java.nio.file.Path findLibrary(String libraryName) {
        LOGGER.info("Resolved path: {}", workingDirectory.resolve(libraryName));
        // For now make assumption that the directory is flat:
        return workingDirectory.resolve(libraryName);
    }
}
