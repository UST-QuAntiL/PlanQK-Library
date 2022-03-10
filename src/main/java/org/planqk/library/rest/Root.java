package org.planqk.library.rest;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@OpenAPIDefinition
@Path("/")
public class Root {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getText() {
        return "<h1>Server runs</h1>";
    }
}
