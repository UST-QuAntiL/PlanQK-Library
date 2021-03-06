package org.planqk.library.rest.base;

import java.io.IOException;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.planqk.library.core.properties.ServerPropertyService;
import org.planqk.library.core.repository.LibraryService;
import org.planqk.library.rest.model.NewLibraryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("libraries")
@Tag(name = "Libraries")
public class Libraries {
    private final LibraryService libraryService;
    private final Logger LOGGER = LoggerFactory.getLogger(Libraries.class);

    public Libraries() {
        libraryService = LibraryService.getInstance(ServerPropertyService.getInstance().getWorkingDirectory());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getLibraryNames() throws IOException {
        try {
            return libraryService.getLibraryNames();
        } catch (IOException e) {
            LOGGER.error("Error retrieving library names.", e);
            throw e;
        }
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    public Response createNewLibrary(NewLibraryDTO newLibraryConfiguration) throws IOException {
        if (libraryService.libraryExists(newLibraryConfiguration.getLibraryName())) {
            return Response.status(Response.Status.CONFLICT)
                           .entity("The given library name is taken.")
                           .build();
        }
        libraryService.createLibrary(newLibraryConfiguration);
        return Response.ok()
                       .build();
    }

    @Path("{libraryName}")
    public Library getLibraryResource(@PathParam("libraryName") String libraryName) {
        return new Library(libraryName);
    }
}
