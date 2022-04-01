package org.planqk.library.core.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jabref.logic.crawler.Crawler;
import org.jabref.logic.crawler.StudyYamlParser;
import org.jabref.logic.git.SlrGitHandler;
import org.jabref.logic.importer.ParseException;
import org.jabref.model.entry.BibEntryTypesManager;
import org.jabref.model.study.Study;
import org.jabref.model.util.DummyFileUpdateMonitor;
import org.jabref.preferences.JabRefPreferences;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//TODO: Write tests for these
public class StudyService {
    private static final Map<Path, StudyService> instances = new HashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(StudyService.class);
    // Contains all running tasks,
    private final Map<String, CrawlTask> runningCrawls = new HashMap<>();
    private Path studiesDirectory;

    /**
     * Returns a study service instance for the selected working directory
     *
     * @param workingDirectory the path under which the studies can be found in the studies directory
     */
    private StudyService(Path workingDirectory) {
        this.studiesDirectory = workingDirectory.resolve("studies");
        if (Files.notExists(studiesDirectory)) {
            try {
                Files.createDirectories(studiesDirectory);
            } catch (IOException e) {
                LOGGER.error("Could not create working directory.", e);
                System.exit(1);
            }
        }
    }

    public static synchronized StudyService getInstance(Path workingDirectory) {
        return instances.computeIfAbsent(workingDirectory, StudyService::new);
    }

    /**
     * Each study is managed as a directory within the study directory
     *
     * @return A list of the currently existing studies
     */
    public List<String> getStudyNames() throws IOException {
        return Files.list(studiesDirectory)                // Alternatively walk(Path start, int depth) for recursive aggregation
                    .filter(Files::isDirectory)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
    }

    public synchronized void createStudy(Study study) throws IOException {
        Files.createDirectories(studiesDirectory.resolve(Paths.get(study.getTitle())));
        StudyYamlParser parser = new StudyYamlParser();
        parser.writeStudyYamlFile(study, studiesDirectory.resolve(Paths.get(study.getTitle(), "study.yml")));
    }

    public boolean deleteStudy(String studyName) throws IOException {
        Path study = studiesDirectory.resolve(Paths.get(studyName));
        if (Files.notExists(study)) {
            return false;
        }
        FileUtils.deleteDirectory(study.toFile());
        return true;
    }

    public boolean studyExists(String studyName) {
        return Files.exists(getStudyPath(studyName));
    }

    /**
     * Starts a crawl for a specific study
     *
     * @return Returns whether a crawl was started, returns false if a crawl for the specified study is already running
     * @throws ParseException Occurs if the study definition file is malformed
     */
    public synchronized Boolean startCrawl(String studyName) throws IOException, ParseException {
        if (runningCrawls.containsKey(studyName)) {
            return false;
        }
        Path studyDirectory = studiesDirectory.resolve(Paths.get(studyName));
        CrawlTask crawl = new CrawlTask(new Crawler(studyDirectory, new SlrGitHandler(studyDirectory), JabRefPreferences.getInstance().getGeneralPreferences(), JabRefPreferences.getInstance().getImportFormatPreferences(), JabRefPreferences.getInstance().getSavePreferences(), new BibEntryTypesManager(), new DummyFileUpdateMonitor()));
        runningCrawls.put(studyName, crawl);
        new Thread(crawl).start();
        return true;
    }

    /**
     * Checks whether there is a crawl running for the specified study.
     * Removes crawls that are finished or failed.
     */
    public Boolean isCrawlRunning(String studyName) {
        if (!runningCrawls.containsKey(studyName)) {
            return false;
        }
        if (runningCrawls.get(studyName).getStatus() == TaskStatus.RUNNING) {
            return true;
        }
        runningCrawls.remove(studyName);
        return false;
    }

    public Path getStudyPath(String studyName) {
        return studiesDirectory.resolve(Paths.get(studyName));
    }

    public Study getStudyDefinition(String studyName) throws IOException {
        StudyYamlParser parser = new StudyYamlParser();
        return parser.parseStudyYamlFile(studiesDirectory.resolve(Paths.get(studyName, "study.yml")));
    }
}
