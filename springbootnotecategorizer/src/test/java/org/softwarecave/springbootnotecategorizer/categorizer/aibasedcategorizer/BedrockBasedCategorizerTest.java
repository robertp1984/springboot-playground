package org.softwarecave.springbootnotecategorizer.categorizer.aibasedcategorizer;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.softwarecave.springbootnotecategorizer.categorizer.Categorizer;
import org.softwarecave.springbootnotecategorizer.categorizer.CategorizerFactory;
import org.softwarecave.springbootnotecategorizer.categorizer.CategorizerResult;
import org.softwarecave.springbootnotecategorizer.categorizer.CategorizerResults;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * This test depends on the real external service (Amazon Bedrock) and is more of an integration test than a unit test.
 * Due to the nature of the AI the results are not deterministic and the results may vary between executions.
 * Therefore, the assertion rules are more relaxed and the test may fail from time to time.
 */
public class BedrockBasedCategorizerTest {
    @ParameterizedTest
    @CsvSource(value = {
            "Pushing to Git repository, Use git push to upload your local repository content to a remote., GIT",
            "Creating a new Java project, Use Maven to create a new Java project., JAVA",
            "Create topic in Apache Kafka, Use kafka-topics command to create a new topic., KAFKA",
            "Using Spring Data JPA, Use Spring Data JPA to simplify database access in your Spring application., SPRING|JPA",
            "Dockerizing a Spring Boot application, Use Docker to containerize your Spring Boot application., SPRING|DOCKER"
    })
    public void testCategorizeParams(String title, String body, String expectedCategoriesString) {
        Categorizer categorizer = new CategorizerFactory().getBedrockBasedCategorizer();
        CategorizerResults categories = categorizer.categorize(title, body);
        assertThat(categories).isNotNull();

        List<String> expectedCategories = List.of(expectedCategoriesString.split("\\|"));
        int expectedCategoriesCount = expectedCategories.size();

        List<CategorizerResult> topKResults = categories.getTopKResults(expectedCategoriesCount);
        assertThat(topKResults).hasSize(expectedCategoriesCount);
        for (int i = 0; i < expectedCategoriesCount; i++) {
            assertThat(topKResults.get(i).getCategory().name()).isIn(expectedCategories);
            assertThat(topKResults.get(i).getScore()).isGreaterThan(0.2);
        }
    }

}
