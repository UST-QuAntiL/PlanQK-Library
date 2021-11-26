package rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("")
public class Root {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getText() {
        return "<h1>Test successful</h1>.";
    }
}