package org.softwarecave.springbootnotecategorizer;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import lombok.extern.slf4j.Slf4j;
import org.softwarecave.springbootnotecategorizer.categorizer.Categorizer;
import org.softwarecave.springbootnotecategorizer.categorizer.CategorizerResults;

import java.util.List;

@Slf4j
public class StickyNoteModifier {
    public static final String TITLE_PATH = "$.title";
    public static final String BODY_PATH = "$.body";

    private final DocumentContext documentContext;
    private final Categorizer categorizer;

    public StickyNoteModifier(String objectJson, Categorizer categorizer) {
        Configuration configuration = Configuration.defaultConfiguration()
                .addOptions(Option.SUPPRESS_EXCEPTIONS);

        documentContext = JsonPath.parse(objectJson, configuration);
        this.categorizer = categorizer;
    }

    public StickyNoteModifier addCategories(int numberOfCategories) {
        var title = documentContext.read(TITLE_PATH, String.class);
        var body = documentContext.read(BODY_PATH, String.class);
        if (title == null || body == null) {
            log.error("Object does not have title or body, cannot categorize");
            return this;
        }

        CategorizerResults categorizerResults = categorizer.categorize(title, body);
        List<String> categoryNames = categorizerResults.getTopKResults(numberOfCategories)
                .stream()
                .map(r -> r.getCategory().getName())
                .toList();

        documentContext.put("$", "categories", categoryNames);
        return this;
    }

    public String getModifiedObjectJson() {
        return documentContext.jsonString();
    }
}

