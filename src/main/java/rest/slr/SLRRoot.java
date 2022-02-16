package rest.slr;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

public class SLRRoot {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getText() {
        return "<h1>Server runs</h1>";
    }
}
