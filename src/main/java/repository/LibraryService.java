package repository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jabref.logic.database.DatabaseMerger;
import org.jabref.logic.exporter.AtomicFileWriter;
import org.jabref.logic.exporter.BibtexDatabaseWriter;
import org.jabref.logic.exporter.SavePreferences;
import org.jabref.logic.importer.OpenDatabase;
import org.jabref.model.database.BibDatabase;
import org.jabref.model.database.BibDatabaseContext;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.BibEntryTypesManager;
import org.jabref.model.util.DummyFileUpdateMonitor;
import org.jabref.model.util.FileUpdateMonitor;
import org.jabref.preferences.JabRefPreferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LibraryService {
    private static final Map<Path, LibraryService> instances = new HashMap<>();
    private Path workingDirectory;

    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryService.class);

    private LibraryService(Path workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public static LibraryService getInstance(Path workingDirectory) {
        return instances.computeIfAbsent(workingDirectory, LibraryService::new);
    }

    public List<String> getLibraryNames() throws IOException {
        return Files.list(workingDirectory)                // Alternatively walk(Path start, int depth) for recursive aggregation
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(file -> file.endsWith(".bib"))
                    .collect(Collectors.toList());
    }


    public void createLibrary(String libraryName) throws IOException {
        Files.createFile(getLibraryPath(libraryName));
    }

    public Boolean deleteLibrary(String libraryName) throws IOException {
        return Files.deleteIfExists(getLibraryPath(libraryName));
    }

    public boolean libraryExists(String libraryName) {
        return Files.exists(getLibraryPath(libraryName));
    }

    public List<BibEntry> getLibraryEntries(String libraryName) throws IOException {
        Path libraryPath = getLibraryPath(libraryName);
        if (!Files.exists(libraryPath)) {
            throw new FileNotFoundException();
        }
        // We do not need any update monitoring
        return new ArrayList<>(OpenDatabase.loadDatabase(libraryPath, JabRefPreferences.getInstance().getImportFormatPreferences(), new DummyFileUpdateMonitor())
                                           .getDatabase()
                                           .getEntries());
    }

    public Optional<BibEntry> getLibraryEntryMatchingCiteKey(String libraryName, String citeKey) throws IOException {
        Path libraryPath = getLibraryPath(libraryName);
        if (!Files.exists(libraryPath)) {
            throw new FileNotFoundException();
        }
        // Note that this might lead to issues if multiple entries have the same cite key!
        // We do not need any update monitoring
        return OpenDatabase.loadDatabase(libraryPath, JabRefPreferences.getInstance().getImportFormatPreferences(), new DummyFileUpdateMonitor())
                           .getDatabase()
                           .getEntryByCitationKey(citeKey);
    }

    public void addEntryToLibrary(String libraryName, BibEntry newEntry) throws IOException {
        Path libraryPath = getLibraryPath(libraryName);
        BibDatabaseContext context;
        if (!Files.exists(libraryPath)) {
            Files.createFile(libraryPath);
            context = new BibDatabaseContext(new BibDatabase());
        } else {
            context = OpenDatabase.loadDatabase(libraryPath, JabRefPreferences.getInstance().getImportFormatPreferences(), new DummyFileUpdateMonitor())
                                  .getDatabaseContext();
        }
        context.getDatabase().insertEntry(newEntry);
        SavePreferences savePreferences = JabRefPreferences.getInstance().getSavePreferences();

        try (AtomicFileWriter fileWriter = new AtomicFileWriter(libraryPath, savePreferences.getEncoding(), savePreferences.shouldMakeBackup())) {
            BibtexDatabaseWriter databaseWriter = new BibtexDatabaseWriter(fileWriter, savePreferences, new BibEntryTypesManager());
            databaseWriter.saveDatabase(context);
        }
    }

    public boolean deleteEntryByCiteKey(String libraryName, String citeKey) throws IOException {
        Path libraryPath = getLibraryPath(libraryName);
        BibDatabaseContext context;
        if (!Files.exists(libraryPath)) {
            return false;
        } else {
            context = OpenDatabase.loadDatabase(libraryPath, JabRefPreferences.getInstance().getImportFormatPreferences(), new DummyFileUpdateMonitor())
                                  .getDatabaseContext();
        }
        Optional<BibEntry> entry = context.getDatabase().getEntryByCitationKey(citeKey);
        if (entry.isEmpty()) {
            return false;
        }

        context.getDatabase().removeEntry(entry.get());
        SavePreferences savePreferences = JabRefPreferences.getInstance().getSavePreferences();

        try (AtomicFileWriter fileWriter = new AtomicFileWriter(libraryPath, savePreferences.getEncoding(), savePreferences.shouldMakeBackup())) {
            BibtexDatabaseWriter databaseWriter = new BibtexDatabaseWriter(fileWriter, savePreferences, new BibEntryTypesManager());
            databaseWriter.saveDatabase(context);
            return true;
        }
    }

    public List<BibEntry> getAllEntries() throws IOException {
        LOGGER.info("Starting merge");
        List<String> libraryNames = getLibraryNames();
        LOGGER.info("Starting merge");
        BibDatabase result = new BibDatabase();
        DatabaseMerger merger = new DatabaseMerger(JabRefPreferences.getInstance().getImportFormatPreferences().getKeywordSeparator());
        LOGGER.info(libraryNames.toString());
        FileUpdateMonitor dummy = new DummyFileUpdateMonitor();
        libraryNames.stream()
                    .map(this::getLibraryPath)
                    .map(path -> {
                        try {
                            LOGGER.info("Opening database...");
                            return OpenDatabase.loadDatabase(path, JabRefPreferences.getInstance().getImportFormatPreferences(), dummy).getDatabase();
                        } catch (IOException e) {
                            // Just return an empty database, a.k.a if opening fails, ignore it
                            return new BibDatabase();
                        }
                    })
                    .forEach(database -> merger.merge(result, database));

        LOGGER.info("Ending merge");
        return new ArrayList<>(result.getEntries());
    }

    private Path getLibraryPath(String libraryName) {
        libraryName = addBibExtensionIfMissing(libraryName);
        LOGGER.info("Resolved path: {}", workingDirectory.resolve(libraryName));
        // For now make assumption that the directory is flat:
        return workingDirectory.resolve(libraryName);
    }

    private String addBibExtensionIfMissing(String libraryName) {
        return libraryName.endsWith(".bib") ? libraryName : libraryName + ".bib";
    }
}
