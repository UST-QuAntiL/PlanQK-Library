package org.planqk.library.rest.slr;

import java.io.IOException;

import org.jabref.logic.importer.ParseException;
import org.jabref.model.study.Study;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.planqk.library.core.properties.ServerPropertyService;
import org.planqk.library.core.repository.StudyService;
import org.planqk.library.rest.base.Libraries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO: Test all endpoints here
@Tag(name = "Systematic Literature Review")
public class Studies {
    private final StudyService studyService;
    private static final Logger LOGGER = LoggerFactory.getLogger(Studies.class);

    public Studies() {
        studyService = StudyService.getInstance(ServerPropertyService.getInstance().getWorkingDirectory());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getText() {
        try {
            return Response.ok(studyService.getStudyNames())
                           .build();
        } catch (IOException e) {
            return Response.serverError()
                           .entity(e.getMessage())
                           .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createStudy(Study study) {
        try {
            if (studyService.studyExists(study.getTitle())) {
                return Response.status(Response.Status.CONFLICT).entity("The given study name is already in use.").build();
            }
            studyService.createStudy(study);
            return Response.ok("Study directory was setup.")
                           .build();
        } catch (IOException e) {
            return Response.serverError()
                           .entity(e.getMessage())
                           .build();
        }
    }

    @DELETE
    @Path("{studyName}")
    public Response deleteStudy(@PathParam("studyName") String studyName) {
        try {
            studyService.deleteStudy(studyName);
            return Response.ok("Study deleted.")
                           .build();
        } catch (IOException e) {
            return Response.serverError()
                           .entity(e.getMessage())
                           .build();
        }
    }

    @POST
    @Path("{studyName}/run-study")
    public Response crawlStudy(@PathParam("studyName") String studyName) {
        try {
            Boolean crawlStarted = studyService.startCrawl(studyName);
            if (crawlStarted) {
                // TODO: Provide him with {studyName}/crawl to check status -> How?
                return Response.ok("Crawl started.")
                               .build();
            }
            // TODO: Provide him with {studyName}/crawl to check status
            return Response.ok("Crawl was already running, no new run started.")
                           .build();
        } catch (IOException | ParseException e) {
            return Response.serverError()
                           .entity(e.getMessage())
                           .build();
        }
    }

    @GET
    @Path("{studyName}/crawl")
    public Response getCrawlStatus(@PathParam("studyName") String studyName) {
        if (studyService.isCrawlRunning(studyName)) {
            // TODO: Provide him with {studyName}/results to get results later
            return Response.ok("Crawl currently running")
                           .build();
        }
        // TODO: Provide him with {studyName}/results to get results later
        return Response.ok("No crawl currently running.")
                       .build();
    }

    @GET
    @Path("{studyName}")
    public void getStudyResults(@PathParam("studyName") String studyName) {
        //TODO:
        return;
    }
}
