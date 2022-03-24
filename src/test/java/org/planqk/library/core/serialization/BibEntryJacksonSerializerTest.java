package org.planqk.library.core.serialization;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.SpecialField;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.types.StandardEntryType;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BibEntryJacksonSerializerTest {

    @Test
    void write() throws IOException {
        BibEntry entry = getEntriesLib1().get(0);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new SimpleModule().addSerializer(new BibEntryJacksonSerializer(BibEntry.class)));
        String json = mapper.writeValueAsString(entry);
        assertEquals("{\"entrytype\":\"Article\",\"citekey\":\"Saha2018\",\"author\":\"Prashanta Saha and Upulee Kanewala\",\"date\":\"2018-02-20\",\"title\":\"Fault Detection Effectiveness of Source Test Case Generation Strategies for Metamorphic Testing\"}", json);
    }

    @Test
    void writeSpecial() throws IOException {
        BibEntry entry = getEntriesLib1().get(1);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new SimpleModule().addSerializer(new BibEntryJacksonSerializer(BibEntry.class)));
        String json = mapper.writeValueAsString(entry);
        assertEquals("{\"entrytype\":\"Article\",\"citekey\":\"Sanchez2016\",\"author\":\"Jimi Sanchez\",\"date\":\"2016-06-01\",\"priority\":\"prio1\",\"title\":\"A Review of Pair-wise Testing\"}", json);
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
                .withField(SpecialField.PRIORITY, "prio1")
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
