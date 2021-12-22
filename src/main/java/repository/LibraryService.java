package repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jabref.logic.exporter.AtomicFileWriter;
import org.jabref.logic.exporter.BibtexDatabaseWriter;
import org.jabref.logic.exporter.SavePreferences;
import org.jabref.logic.importer.OpenDatabase;
import org.jabref.model.database.BibDatabase;
import org.jabref.model.database.BibDatabaseContext;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.BibEntryTypesManager;
import org.jabref.model.util.DummyFileUpdateMonitor;
import org.jabref.preferences.JabRefPreferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rest.Libraries;

public class LibraryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(Libraries.class);
    java.nio.file.Path workingDirectory;

    public LibraryService(Path workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public List<String> getLibraryNames() throws IOException {
        return Files.list(workingDirectory)                // Alternatively walk(Path start, int depth) for recursive aggregation
                    .filter(file -> !Files.isDirectory(file))
                    .map(java.nio.file.Path::getFileName)
                    .map(java.nio.file.Path::toString)
                    .filter(file -> file.endsWith(".bib"))
                    .collect(Collectors.toList());
    }

    public List<BibEntry> getLibraryEntries(String libraryName) throws IOException {
        Path libraryPath = getLibraryPath(libraryName);
        // We do not need any update monitoring
        return new ArrayList<>(OpenDatabase.loadDatabase(libraryPath, JabRefPreferences.getInstance().getImportFormatPreferences(), new DummyFileUpdateMonitor())
                                           .getDatabase()
                                           .getEntries());
    }

    public void addEntryToLibrary(String libraryName, BibEntry newEntry) throws IOException {
        Path libraryPath = getLibraryPath(libraryName);
        BibDatabase database;
        if (!Files.exists(libraryPath)) {
            Files.createFile(libraryPath);
            database = new BibDatabase();
        } else {
            database = OpenDatabase.loadDatabase(libraryPath, JabRefPreferences.getInstance().getImportFormatPreferences(), new DummyFileUpdateMonitor())
                                   .getDatabaseContext().getDatabase();
        }

        database.insertEntry(newEntry);
        SavePreferences savePreferences = JabRefPreferences.getInstance().getSavePreferences();

        try (AtomicFileWriter fileWriter = new AtomicFileWriter(libraryPath, savePreferences.getEncoding(), savePreferences.shouldMakeBackup())) {
            BibtexDatabaseWriter databaseWriter = new BibtexDatabaseWriter(fileWriter, savePreferences, new BibEntryTypesManager());
            databaseWriter.saveDatabase(new BibDatabaseContext(database));
        }
    }

    public Boolean deleteLibrary(String libraryName) throws IOException {
        java.nio.file.Path pathToLibrary = getLibraryPath(libraryName);
        return Files.deleteIfExists(pathToLibrary);
    }

    public boolean libraryExists(String libraryName) {
        return Files.exists(getLibraryPath(libraryName));
    }

    private java.nio.file.Path getLibraryPath(String libraryName) {
        libraryName = addBibExtensionIfMissing(libraryName);
        LOGGER.info("Resolved path: {}", workingDirectory.resolve(libraryName));
        // For now make assumption that the directory is flat:
        return workingDirectory.resolve(libraryName);
    }

    private String addBibExtensionIfMissing(String libraryName) {
        return libraryName.endsWith(".bib") ? libraryName : libraryName + ".bib";
    }
}
