package org.softwarecave.springbootnotecategorizer.categorizer.keywords;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.softwarecave.springbootnotecategorizer.categorizer.Categorizer;
import org.softwarecave.springbootnotecategorizer.categorizer.CategorizerFactory;
import org.softwarecave.springbootnotecategorizer.categorizer.CategorizerResult;
import org.softwarecave.springbootnotecategorizer.categorizer.CategorizerResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SimpleKeywordsCategorizerTest {

    @Autowired
    private CategorizerFactory categorizerFactory;

    @ParameterizedTest
    @CsvSource(value = {
            "Pushing to Git repository, Use git push to upload your local repository content to a remote., GIT",
            "Creating a new Java project, Use Maven to create a new Java project., JAVA",
            "Create topic in Apache Kafka, Use kafka-topics command to create a new topic., KAFKA",
            "Using Spring Data JPA, Use Spring Data JPA to simplify database access in your Spring application., SPRING|JPA",
            "Dockerizing a Spring Boot application, Use Docker to containerize your Spring Boot application., SPRING|DOCKER"
    })
    public void testCategorizeParams(String title, String body, String expectedCategoriesString) {
        Categorizer categorizer = categorizerFactory.getKeywordBasedCategorizer();
        CategorizerResults categories = categorizer.categorize(title, body);
        assertThat(categories).isNotNull();

        String[] expectedCategories = expectedCategoriesString.split("\\|");
        int expectedCategoriesCount = expectedCategories.length;

        List<CategorizerResult> topKResults = categories.getTopKResults(expectedCategoriesCount);
        assertThat(topKResults).hasSize(expectedCategoriesCount);
        for (int i = 0; i < expectedCategoriesCount; i++) {
            assertThat(topKResults.get(i).category().name()).isEqualTo(expectedCategories[i]);
            assertThat(topKResults.get(i).score()).isGreaterThan(0.2);
        }
    }

}
