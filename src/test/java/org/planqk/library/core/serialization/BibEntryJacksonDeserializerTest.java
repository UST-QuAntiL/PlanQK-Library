package org.planqk.library.core.serialization;

import java.io.IOException;
import java.util.List;

import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.SpecialField;
import org.jabref.model.entry.field.SpecialFieldValue;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.field.UnknownField;
import org.jabref.model.entry.types.StandardEntryType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BibEntryJacksonDeserializerTest {

    @Test
    void read() throws IOException {
        String json = "{\"entrytype\":\"Article\",\"citekey\":\"Saha2018\",\"author\":\"Prashanta Saha and Upulee Kanewala\",\"date\":\"2018-02-20\",\"title\":\"Fault Detection Effectiveness of Source Test Case Generation Strategies for Metamorphic Testing\"}";

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new SimpleModule().addDeserializer(BibEntry.class, new BibEntryJacksonDeserializer()));
        assertEquals(getEntriesLib1().get(0), mapper.readValue(json, BibEntry.class));
    }

    @Test
    void parseUnkownField() throws IOException {
        String json = "{\"123\":\"456\"}";
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new SimpleModule().addDeserializer(BibEntry.class, new BibEntryJacksonDeserializer()));

        BibEntry deserializedEntry = mapper.readValue(json, BibEntry.class);
        assertEquals("456", deserializedEntry.getField(new UnknownField("123")).get());
    }

    @Test
    void parseSpecialField() throws IOException {
        String json = "{\"priority\":\"prio1\"}";
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new SimpleModule().addDeserializer(BibEntry.class, new BibEntryJacksonDeserializer()));

        BibEntry deserializedEntry = mapper.readValue(json, BibEntry.class);
        assertEquals(SpecialFieldValue.PRIORITY_HIGH.getFieldValue().get(), deserializedEntry.getField(SpecialField.PRIORITY).get());
    }

    @Test
    void testRoundTrip() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new SimpleModule().addSerializer(new BibEntryJacksonSerializer(BibEntry.class))
                                                .addDeserializer(BibEntry.class, new BibEntryJacksonDeserializer()));
        BibEntryJacksonSerializer serializer = new BibEntryJacksonSerializer(BibEntry.class);
        for (BibEntry entry : getEntriesLib1()) {
            assertEquals(entry, mapper.readValue(mapper.writeValueAsString(entry), BibEntry.class));
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
