package org.planqk.library.core.serialization;

import java.util.List;

import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.types.StandardEntryType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.planqk.library.core.serialization.BibEntryAdapter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BibEntryAdapterTest {

    @Test
    void write() {
        BibEntry entry = getEntriesLib1().get(0);
        Gson gson = new GsonBuilder().registerTypeAdapter(BibEntry.class, new BibEntryAdapter()).create();
        String json = gson.toJson(entry);
    }

    @Test
    void read() {
        String json = "{\"entrytype\":\"Article\",\"citekey\":\"Saha2018\",\"author\":\"Prashanta Saha and Upulee Kanewala\",\"date\":\"2018-02-20\",\"title\":\"Fault Detection Effectiveness of Source Test Case Generation Strategies for Metamorphic Testing\"}";
        Gson gson = new GsonBuilder().registerTypeAdapter(BibEntry.class, new BibEntryAdapter()).create();
        BibEntry deserializedEntry = gson.fromJson(json, BibEntry.class);
        assertEquals(getEntriesLib1().get(0), deserializedEntry);
    }

    @Test
    void testRoundTrip() {
        Gson gson = new GsonBuilder().registerTypeAdapter(BibEntry.class, new BibEntryAdapter()).create();
        for (BibEntry entry: getEntriesLib1()) {
            assertEquals(entry, gson.fromJson(gson.toJson(entry), BibEntry.class));
        }
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
}
