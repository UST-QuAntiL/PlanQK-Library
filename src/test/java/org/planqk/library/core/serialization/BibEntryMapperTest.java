package org.planqk.library.core.serialization;

import java.util.List;

import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.types.StandardEntryType;

import org.junit.jupiter.api.Test;
import org.planqk.library.rest.model.BibEntryDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BibEntryMapperTest {

    @Test
    public void testEntryToDTO() {
        List<BibEntry> entries = getEntries();
        assertEquals(getDTOEntries().get(0), BibEntryMapper.map(entries.get(0)));
        assertEquals(getDTOEntries().get(1), BibEntryMapper.map(entries.get(1)));
        assertEquals(getDTOEntries().get(2), BibEntryMapper.map(entries.get(2)));
    }

    @Test
    public void testDTOToEntry() {
        List<BibEntryDTO> entries = getDTOEntries();
        assertEquals(getEntries().get(0), BibEntryMapper.map(entries.get(0)));
        assertEquals(getEntries().get(1), BibEntryMapper.map(entries.get(1)));
        assertEquals(getEntries().get(2), BibEntryMapper.map(entries.get(2)));
    }

    @Test
    public void testRoundTrip() {
        for (BibEntry entry : getEntries()) {
            assertEquals(entry, BibEntryMapper.map(BibEntryMapper.map(entry)));
        }
    }

    private List<BibEntry> getEntries() {
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
        BibEntry entry3 = new BibEntry(StandardEntryType.Book)
                .withCitationKey("Wu2007")
                .withField(StandardField.AUTHOR, "Cheng-Wen Wu")
                .withField(StandardField.YEAR, "2007")
                .withField(StandardField.TITLE, "SOC Testing Methodology and Practice");
        return List.of(entry1, entry2, entry3);
    }

    private List<BibEntryDTO> getDTOEntries() {
        BibEntryDTO entry1 = new BibEntryDTO();
        entry1.entryType = "article";
        entry1.citationKey = "Saha2018";
        entry1.author = "Prashanta Saha and Upulee Kanewala";
        entry1.date = "2018-02-20";
        entry1.title = "Fault Detection Effectiveness of Source Test Case Generation Strategies for Metamorphic Testing";

        BibEntryDTO entry2 = new BibEntryDTO();
        entry2.entryType = "article";
        entry2.citationKey = "Sanchez2016";
        entry2.author = "Jimi Sanchez";
        entry2.date = "2016-06-01";
        entry2.title = "A Review of Pair-wise Testing";

        BibEntryDTO entry3 = new BibEntryDTO();
        entry3.entryType = "book";
        entry3.citationKey = "Wu2007";
        entry3.author = "Cheng-Wen Wu";
        entry3.year = "2007";
        entry3.title = "SOC Testing Methodology and Practice";

        return List.of(entry1, entry2, entry3);
    }
}
