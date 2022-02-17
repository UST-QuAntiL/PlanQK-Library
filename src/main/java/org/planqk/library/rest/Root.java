package org.planqk.library.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.planqk.library.rest.base.Libraries;
import org.planqk.library.rest.slr.SLRRoot;
import org.planqk.library.rest.base.Accumulation;

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
