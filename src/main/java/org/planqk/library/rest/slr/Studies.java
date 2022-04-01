package org.planqk.library.rest.slr;

import java.io.IOException;

import org.jabref.logic.importer.ParseException;

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
import org.planqk.library.rest.base.Library;
import org.planqk.library.rest.model.CrawlStatus;
import org.planqk.library.rest.model.StudyDTO;
import org.planqk.library.rest.model.StudyNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO: Test all endpoints here
@Path("studies")
@Tag(name = "Systematic Literature Review")
public class Studies {
    private final StudyService studyService;
    private static final Logger LOGGER = LoggerFactory.getLogger(Studies.class);

    public Studies() {
        studyService = StudyService.getInstance(ServerPropertyService.getInstance().getWorkingDirectory());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStudyNames() {
        try {
            return Response.ok(new StudyNames(studyService.getStudyNames()))
                           .build();
        } catch (IOException e) {
            LOGGER.error("Error retrieving study names.", e);
            return Response.serverError()
                           .entity(e.getMessage())
                           .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createStudy(StudyDTO study) {
        try {
            if (studyService.studyExists(study.studyDefinition.getTitle())) {
                return Response.status(Response.Status.CONFLICT).entity("The given study name is already in use.").build();
            }
            studyService.createStudy(study.studyDefinition);
            return Response.ok()
                           .build();
        } catch (IOException e) {
            LOGGER.error("Error retrieving study names.", e);
            return Response.serverError()
                           .entity(e.getMessage())
                           .build();
        }
    }

    // TODO: How can we remove the /results
    @Path("{studyName}/results")
    public Library getStudyResults(@PathParam("studyName") String studyName) {
        return new Library(studyService.getStudyPath(studyName), "studyResult");
    }

    @DELETE
    @Path("{studyName}")
    public Response deleteStudy(@PathParam("studyName") String studyName) {
        try {
            studyService.deleteStudy(studyName);
            return Response.ok()
                           .build();
        } catch (IOException e) {
            LOGGER.error("Error deleting study.", e);
            return Response.serverError()
                           .entity(e.getMessage())
                           .build();
        }
    }

    @POST
    @Path("{studyName}/crawl")
    public Response crawlStudy(@PathParam("studyName") String studyName) {
        try {
            Boolean crawlStarted = studyService.startCrawl(studyName);
            if (crawlStarted) {
                return Response.ok("Crawl started.")
                               .build();
            }
            return Response.ok("Crawl was already running, no new run started.")
                           .build();
        } catch (IOException | ParseException e) {
            LOGGER.error("Error during crawling", e);
            return Response.serverError()
                           .entity(e.getMessage())
                           .build();
        }
    }

    @GET
    @Path("{studyName}/crawl")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCrawlStatus(@PathParam("studyName") String studyName) {
        if (studyService.isCrawlRunning(studyName)) {
            return Response.ok(new CrawlStatus(true))
                           .build();
        }
        return Response.ok(new CrawlStatus(true))
                       .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{studyName}/studyDefinition")
    public Response getStudyDefinition(@PathParam("studyName") String studyName) {
        try {
            return Response.ok()
                           .entity(new StudyDTO(studyService.getStudyDefinition(studyName)))
                           .build();
        } catch (IOException e) {
            LOGGER.error("Error retrieving study definition.", e);
            return Response.serverError()
                           .entity(e.getMessage())
                           .build();
        }
    }

    // TODO: Add put
}
