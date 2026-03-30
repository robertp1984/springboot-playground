package org.softwarecave.springbootnotecategorizer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.softwarecave.springbootnotecategorizer.categorizer.Categorizer;
import org.softwarecave.springbootnotecategorizer.categorizer.CategorizerResults;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StickyNoteModifierTest {

    @Mock
    private Categorizer categorizer;

    @Test
    public void testStickyNoteModifier() {
        CategorizerResults categorizerResults = new CategorizerResults();
        categorizerResults.addResult(Category.CLOUD, 0.8);
        when(categorizer.categorize(anyString(), anyString())).thenReturn(categorizerResults);

        String inputJson = """
                {
                    "title": "Cloud Note",
                    "body": "This is a note about cloud computing.",
                    "otherField": "This field should remain unchanged."
                }
                """;

        String modifiedObjectJson = new StickyNoteModifier(inputJson, categorizer)
                .addCategories(1)
                .getModifiedObjectJson();

        String expectedJson = """
                {
                    "title": "Cloud Note",
                    "body": "This is a note about cloud computing.",
                    "otherField": "This field should remain unchanged.",
                    "categories": ["Cloud"]
                }
                """;

        assertThat(modifiedObjectJson)
                .isNotNull()
                .isEqualToIgnoringWhitespace(expectedJson);
    }


    @Test
    public void testStickyNoteModifier_NoCategories() {
        CategorizerResults categorizerResults = new CategorizerResults();
        when(categorizer.categorize(anyString(), anyString())).thenReturn(categorizerResults);

        String inputJson = """
                {
                    "title": "Cloud Note",
                    "body": "This is a note about cloud computing.",
                    "otherField": "This field should remain unchanged."
                }
                """;

        String modifiedObjectJson = new StickyNoteModifier(inputJson, categorizer)
                .addCategories(1)
                .getModifiedObjectJson();

        String expectedJson = """
                {
                    "title": "Cloud Note",
                    "body": "This is a note about cloud computing.",
                    "otherField": "This field should remain unchanged.",
                    "categories": []
                }
                """;

        assertThat(modifiedObjectJson)
                .isNotNull()
                .isEqualToIgnoringWhitespace(expectedJson);
    }

    @Test
    public void testStickyNoteModifier_MissingTitle() {
        String inputJson = """
                {
                    "body": "This is a note about cloud computing.",
                    "otherField": "This field should remain unchanged."
                }
                """;

        String modifiedObjectJson = new StickyNoteModifier(inputJson, categorizer)
                .addCategories(1)
                .getModifiedObjectJson();

        assertThat(modifiedObjectJson)
                .isNotNull()
                .isEqualToIgnoringWhitespace(inputJson);
    }

    @Test
    public void testStickyNoteModifier_MissingBody() {
        String inputJson = """
                {
                    "title": "Cloud Note",
                    "otherField": "This field should remain unchanged."
                }
                """;

        String modifiedObjectJson = new StickyNoteModifier(inputJson, categorizer)
                .addCategories(1)
                .getModifiedObjectJson();

        assertThat(modifiedObjectJson)
                .isNotNull()
                .isEqualToIgnoringWhitespace(inputJson);
    }

}
