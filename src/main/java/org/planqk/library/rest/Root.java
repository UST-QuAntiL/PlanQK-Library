package org.planqk.library.rest;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@OpenAPIDefinition
@Path("/")
@Tag(name = "Root")
public class Root {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getText() {
        return "<h1>Server runs</h1>";
    }
}
