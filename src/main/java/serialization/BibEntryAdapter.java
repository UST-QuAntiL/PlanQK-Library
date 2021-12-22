package serialization;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jabref.logic.TypedBibEntry;
import org.jabref.model.database.BibDatabaseMode;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.Field;
import org.jabref.model.entry.field.InternalField;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * Used to convert a bibentry object between POJO and JSON
 */
public class BibEntryAdapter extends TypeAdapter<BibEntry> {

    private static final String JSON_TYPE = "entrytype";
    private static final String JSON_KEY = "citekey";

    @Override
    public void write(JsonWriter writer, BibEntry entry) throws IOException {
        if (entry == null) {
            writer.nullValue();
            return;
        }
        writer.beginObject();
        writer.name(JSON_TYPE).value(new TypedBibEntry(entry, BibDatabaseMode.BIBTEX).getTypeForDisplay());
        writer.name(JSON_KEY).value(entry.getCitationKey().orElse(""));

        // Grab field entries and place in map
        Map<String, String> mapFieldToValue = new HashMap<>();
        // determine sorted fields -- all fields lower case
        SortedSet<String> sortedFields = new TreeSet<>();
        for (Map.Entry<Field, String> field : entry.getFieldMap().entrySet()) {
            Field fieldName = field.getKey();
            String fieldValue = field.getValue();
            // JabRef stores the key in the field KEY_FIELD, which must not be serialized
            if (!fieldName.equals(InternalField.KEY_FIELD)) {
                String lowerCaseFieldName = fieldName.getName().toLowerCase(Locale.US);
                sortedFields.add(lowerCaseFieldName);
                mapFieldToValue.put(lowerCaseFieldName, fieldValue);
            }
        }

        // Add to writer
        for (String fieldName : sortedFields) {
            writer.name(fieldName).value(String.valueOf(mapFieldToValue.get(fieldName)).replaceAll("\\r\\n", "\n"));
        }
        writer.endObject();
    }

    @Override
    public BibEntry read(JsonReader in) throws IOException {
        // Potential deserializer if required
        return null;
    }
}
