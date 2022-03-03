package org.planqk.library.core.repository;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.types.StandardEntryType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.planqk.library.rest.base.Libraries;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LibraryServiceTest {
    LibraryService libraryService;
    Path lib1;
    Path lib2;

    @BeforeEach
    public void setupService(@TempDir Path workingDir) throws IOException, URISyntaxException {
        this.libraryService = LibraryService.getInstance(workingDir);
        lib1 = Paths.get(LibraryServiceTest.class.getClassLoader().getResource("org/planqk/library/core/lib1.bib").toURI());
        lib2 = Paths.get(LibraryServiceTest.class.getClassLoader().getResource("org/planqk/library/core/lib2.bib").toURI());
        Files.copy(lib1, workingDir.resolve("lib1.bib"), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(lib2, workingDir.resolve("lib2.bib"), StandardCopyOption.REPLACE_EXISTING);
        assert Files.exists(workingDir.resolve("lib1.bib"));
        assert Files.exists(workingDir.resolve("lib2.bib"));
    }

    @Test
    public void getLibraryNames() throws IOException {
        List<String> sortedNames = libraryService.getLibraryNames();
        sortedNames.sort(String::compareTo);
        assertEquals(List.of("lib1.bib", "lib2.bib"), sortedNames);
    }

    @Test
    public void getLibraryEntriesOnExistingLibrary() throws IOException {
        List<BibEntry> entries = libraryService.getLibraryEntries("lib1.bib");
        List<BibEntry> expectedEntries = getEntriesLib1();
        entries.sort(Comparator.comparing(o -> o.getCitationKey().orElse("")));
        assertEquals(expectedEntries, entries);
    }

    @Test
    public void getLibraryEntriesOnNonExistingLibrary() {
        assertThrows(IOException.class, () -> libraryService.getLibraryEntries("nonexistinglibrary.bib"));
    }

    @Test
    public void checkExistenceOfExistingLibrary() {
        assertTrue(libraryService.libraryExists("lib1.bib"));
    }

    @Test
    public void checkExistenceOfExistingLibraryWithoutExtension() {
        assertTrue(libraryService.libraryExists("lib1"));
    }

    @Test
    public void checkExistenceOfNonExistingLibrary() {
        assertFalse(libraryService.libraryExists("nonexistinglibrary.bib"));
    }

    @Test
    public void deleteLibrary() throws IOException {
        assertTrue(libraryService.libraryExists("lib2"));
        assertTrue(libraryService.deleteLibrary("lib2"));
        assertFalse(libraryService.libraryExists("lib2"));
    }

    @Test
    public void deleteNonExistingLibrary() throws IOException {
        assertFalse(libraryService.libraryExists("nonexistinglibrary.bib"));
        assertFalse(libraryService.deleteLibrary("nonexistinglibrary.bib"));
    }

    @Test
    public void addNewEntryToLibrary() throws IOException {
        BibEntry newEntry = new BibEntry(StandardEntryType.Book)
                .withCitationKey("Harrer2018java")
                .withField(StandardField.AUTHOR, "Harrer, S. and Lenhard, J. and Dietz, L.")
                .withField(StandardField.DATE, "2018-03-20")
                .withField(StandardField.TITLE, "Java by Comparison: Become a Java Craftsman in 70 Examples");
        libraryService.addEntryToLibrary("lib1", newEntry);

        List<BibEntry> currentEntries = libraryService.getLibraryEntries("lib1");
        List<BibEntry> expected = getModifiedEntriesLib1();
        currentEntries.sort(Comparator.comparing(o -> o.getCitationKey().orElse("")));

        assertEquals(expected, currentEntries);
    }

    @Test
    public void getAllEntriesLib1Lib2() throws IOException {
        List<BibEntry> result = libraryService.getAllEntries();
        result.sort(Comparator.comparing(o -> o.getCitationKey().orElse("")));
        assertEquals(6, result.size());
        assertEquals(getMergedEntries(), result);
    }

    @Test
    public void getEntryInLibrary() throws IOException {
        for (BibEntry bibEntry : getEntriesLib1()) {
            // Entry has to exist, thus we do not check with present and instead throw an exception
            assertEquals(bibEntry, libraryService.getLibraryEntryMatchingCiteKey("lib1", bibEntry.getCitationKey().get()).orElseThrow());
        }
    }

    @Test
    public void updateEntryInLibrary() throws IOException {
        BibEntry currentVersion = libraryService.getLibraryEntryMatchingCiteKey("lib1","Saha2018").orElseThrow();
        BibEntry changedEntryWithDifferentDate = new BibEntry(StandardEntryType.Article)
                .withCitationKey("Saha2018")
                .withField(StandardField.AUTHOR, "Prashanta Saha and Upulee Kanewala")
                .withField(StandardField.DATE, "2019-03-12")
                .withField(StandardField.TITLE, "Fault Detection Effectiveness of Source Test Case Generation Strategies for Metamorphic Testing");
        libraryService.updateEntry("lib1", changedEntryWithDifferentDate);

        assertTrue(libraryService.getLibraryEntryMatchingCiteKey("lib1", "Saha2018").isPresent());
        assertEquals(currentVersion.getField(StandardField.AUTHOR).orElseThrow(),
                libraryService.getLibraryEntryMatchingCiteKey("lib1", "Saha2018").orElseThrow().getField(StandardField.AUTHOR).orElse(""));
        assertEquals("2019-03-12",
                libraryService.getLibraryEntryMatchingCiteKey("lib1", "Saha2018").orElseThrow().getField(StandardField.DATE).orElse(""));
    }

    @Test
    public void deleteEntryInLibrary() throws IOException {
        List<BibEntry> entriesWithCiteKeyToDelete = libraryService.getLibraryEntries("lib1").stream()
                                                                  .filter(bibEntry -> bibEntry.getCitationKey().get().equals("Saha2018"))
                                                                  .toList();
        assertTrue(entriesWithCiteKeyToDelete.size() > 0);

        libraryService.deleteEntryByCiteKey("lib1", "Saha2018");

        libraryService.getLibraryEntries("lib1").forEach(bibEntry -> assertNotEquals("Saha2018", bibEntry.getCitationKey()));
    }

    private List<BibEntry> getModifiedEntriesLib1() {
        BibEntry newEntry = new BibEntry(StandardEntryType.Book)
                .withCitationKey("Harrer2018java")
                .withField(StandardField.AUTHOR, "Harrer, S. and Lenhard, J. and Dietz, L.")
                .withField(StandardField.DATE, "2018-03-20")
                .withField(StandardField.TITLE, "Java by Comparison: Become a Java Craftsman in 70 Examples");
        BibEntry entry1 = new BibEntry(StandardEntryType.Article)
                .withCitationKey("Saha2018")
                .withField(StandardField.AUTHOR, "Prashanta Saha and Upulee Kanewala")
                .withField(StandardField.DATE, "2018-02-20")
                .withField(StandardField.TITLE, "Fault Detection Effectiveness of Source Test Case Generation Strategies for Metamorphic Testing");
        BibEntry entry2 = new BibEntry(StandardEntryType.Article)
                .withCitationKey("Sanchez2016")
                .withField(StandardField.AUTHOR, "Jimi Sanchez")
                .withField(StandardField.DATE, "2016-06-01")
                .withField(StandardField.TITLE, "A Review of Pair-wise Testing");
        BibEntry entry3 = new BibEntry(StandardEntryType.Article)
                .withCitationKey("Wu2007")
                .withField(StandardField.AUTHOR, "Cheng-Wen Wu")
                .withField(StandardField.DATE, "2007-10-25")
                .withField(StandardField.TITLE, "SOC Testing Methodology and Practice");
        BibEntry entry4 = new BibEntry(StandardEntryType.Article)
                .withCitationKey("Zhu2019")
                .withField(StandardField.AUTHOR, "Hong Zhu and Ian Bayley and Dongmei Liu and Xiaoyu Zheng")
                .withField(StandardField.DATE, "2019-12-20")
                .withField(StandardField.TITLE, "Morphy: A Datamorphic Software Test Automation Tool");
        return List.of(newEntry, entry1, entry2, entry3, entry4);
    }

    private List<BibEntry> getEntriesLib1() {
        BibEntry entry1 = new BibEntry(StandardEntryType.Article)
                .withCitationKey("Saha2018")
                .withField(StandardField.AUTHOR, "Prashanta Saha and Upulee Kanewala")
                .withField(StandardField.DATE, "2018-02-20")
                .withField(StandardField.TITLE, "Fault Detection Effectiveness of Source Test Case Generation Strategies for Metamorphic Testing");
        BibEntry entry2 = new BibEntry(StandardEntryType.Article)
                .withCitationKey("Sanchez2016")
                .withField(StandardField.AUTHOR, "Jimi Sanchez")
                .withField(StandardField.DATE, "2016-06-01")
                .withField(StandardField.TITLE, "A Review of Pair-wise Testing");
        BibEntry entry3 = new BibEntry(StandardEntryType.Article)
                .withCitationKey("Wu2007")
                .withField(StandardField.AUTHOR, "Cheng-Wen Wu")
                .withField(StandardField.DATE, "2007-10-25")
                .withField(StandardField.TITLE, "SOC Testing Methodology and Practice");
        BibEntry entry4 = new BibEntry(StandardEntryType.Article)
                .withCitationKey("Zhu2019")
                .withField(StandardField.AUTHOR, "Hong Zhu and Ian Bayley and Dongmei Liu and Xiaoyu Zheng")
                .withField(StandardField.DATE, "2019-12-20")
                .withField(StandardField.TITLE, "Morphy: A Datamorphic Software Test Automation Tool");
        return List.of(entry1, entry2, entry3, entry4);
    }

    private List<BibEntry> getMergedEntries() {
        BibEntry entry1 = new BibEntry(StandardEntryType.InProceedings)
                .withCitationKey("Kafton2002")
                .withField(StandardField.AUTHOR, "A. Kafton")
                .withField(StandardField.DATE, "10-10 Oct. 2002")
                .withField(StandardField.TITLE, "Wireless SOC testing: Can RF testing costs be reduced?");
        BibEntry entry2 = new BibEntry(StandardEntryType.InProceedings)
                .withCitationKey("Nigh2002")
                .withField(StandardField.AUTHOR, "P. Nigh")
                .withField(StandardField.DATE, "10-10 Oct. 2002")
                .withField(StandardField.TITLE, "Scan-based testing: the only practical solution for testing ASIC/consumer products");
        BibEntry entry3 = new BibEntry(StandardEntryType.Article)
                .withCitationKey("Saha2018")
                .withField(StandardField.AUTHOR, "Prashanta Saha and Upulee Kanewala")
                .withField(StandardField.DATE, "2018-02-20")
                .withField(StandardField.TITLE, "Fault Detection Effectiveness of Source Test Case Generation Strategies for Metamorphic Testing");
        BibEntry entry4 = new BibEntry(StandardEntryType.Article)
                .withCitationKey("Sanchez2016")
                .withField(StandardField.AUTHOR, "Jimi Sanchez")
                .withField(StandardField.DATE, "2016-06-01")
                .withField(StandardField.TITLE, "A Review of Pair-wise Testing");
        BibEntry entry5 = new BibEntry(StandardEntryType.Article)
                .withCitationKey("Wu2007")
                .withField(StandardField.AUTHOR, "Cheng-Wen Wu")
                .withField(StandardField.DATE, "2007-10-25")
                .withField(StandardField.TITLE, "SOC Testing Methodology and Practice");
        BibEntry entry6 = new BibEntry(StandardEntryType.Article)
                .withCitationKey("Zhu2019")
                .withField(StandardField.AUTHOR, "Hong Zhu and Ian Bayley and Dongmei Liu and Xiaoyu Zheng")
                .withField(StandardField.DATE, "2019-12-20")
                .withField(StandardField.TITLE, "Morphy: A Datamorphic Software Test Automation Tool");
        return List.of(entry1, entry2, entry3, entry4, entry5, entry6);
    }
}
