package org.softwarecave.springbootnotecategorizer.categorizer.ai;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.softwarecave.springbootnotecategorizer.categorizer.CategorizerResult;
import org.softwarecave.springbootnotecategorizer.categorizer.CategorizerResults;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CategorizerResultsParserTest {

    @ParameterizedTest
    @CsvSource(value = {
            "KAFKA=0 GIT=10 JPA=2,GIT|JPA",
            "KAFKA=5 GIT=2 JPA=10,JPA|KAFKA",
            "GIT=10 KAFKA=5 JPA=9 DOCKER=7,GIT|JPA|DOCKER|KAFKA"
    })
    public void test(String text, String expectedCategoriesString) {
        String[] expectedCategories = expectedCategoriesString.split("\\|");
        int expectedCategoriesCount = expectedCategories.length;

        CategorizerResults categorizerResults = CategorizerResultsParser.fromText(text);
        List<CategorizerResult> topKResults = categorizerResults.getTopKResults(expectedCategoriesCount);

        for (int i = 0; i < expectedCategoriesCount; i++) {
            assertThat(topKResults.get(i).category().name()).isEqualTo(expectedCategories[i]);
        }
    }
}
