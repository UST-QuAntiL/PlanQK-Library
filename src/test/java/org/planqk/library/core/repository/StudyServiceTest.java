package org.planqk.library.core.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jabref.logic.importer.ParseException;
import org.jabref.model.study.Study;
import org.jabref.model.study.StudyDatabase;
import org.jabref.model.study.StudyQuery;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StudyServiceTest {
    @TempDir
    Path workDir;
    Path studiesDir;

    @Test
    public void createStudy() throws IOException {
        StudyService service = StudyService.getInstance(workDir);
        studiesDir = workDir.resolve("studies");
        service.createStudy(getStudyDefinition("TestStudy"));
        assertTrue(Files.isDirectory(studiesDir.resolve("TestStudy")));
        assertTrue(Files.isRegularFile(studiesDir.resolve("TestStudy/study.yml")));
    }

    @Test
    public void getStudyNames() throws IOException {
        StudyService service = StudyService.getInstance(workDir);
        studiesDir = workDir.resolve("studies");
        service.createStudy(getStudyDefinition("TestStudy"));
        service.createStudy(getStudyDefinition("TestStudy2"));
        Set<String> studyNames = new HashSet<>(service.getStudyNames());
        assertEquals(Set.of("TestStudy", "TestStudy2"), studyNames);
    }

    @Test
    public void deleteStudy() throws IOException {
        StudyService service = StudyService.getInstance(workDir);
        studiesDir = workDir.resolve("studies");
        service.createStudy(getStudyDefinition("TestStudy"));
        assertTrue(Files.isDirectory(studiesDir.resolve("TestStudy")));

        assertTrue(service.deleteStudy("TestStudy"));
        assertFalse(service.deleteStudy("TestStudy"));
        assertFalse(Files.exists(studiesDir.resolve("TestStudy")));
    }

    public Study getStudyDefinition(String studyName) {
        Study study = new Study();
        study.setTitle(studyName);
        study.setAuthors(List.of("A1", "A2"));
        study.setQueries(List.of(new StudyQuery("testQuery1"), new StudyQuery("testQuery2")));
        study.setDatabases(List.of(new StudyDatabase("arxiv", true), new StudyDatabase("ieeexplore", true)));
        study.setResearchQuestions(List.of("Q1", "Q2"));
        return study;
    }
}
