package rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import rest.base.Accumulation;
import rest.base.Libraries;
import rest.slr.SLRRoot;

@Path("api/")
public class Root {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String getText() {
        return "<h1>Server runs</h1>";
    }

    @Path("libraries/")
    public Libraries getLibrariesResource() {
        return new Libraries();
    }

    @Path("all/")
    public Accumulation getAllEntriesResource() {
        return new Accumulation();
    }

    @Path("slr/")
    public SLRRoot getSLRResource() {
        return new SLRRoot();
    }
}
