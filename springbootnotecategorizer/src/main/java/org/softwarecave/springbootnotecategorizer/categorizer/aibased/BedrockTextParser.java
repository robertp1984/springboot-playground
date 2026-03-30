package org.softwarecave.springbootnotecategorizer.categorizer.aibased;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.softwarecave.springbootnotecategorizer.Category;
import org.softwarecave.springbootnotecategorizer.categorizer.CategorizationException;
import org.softwarecave.springbootnotecategorizer.categorizer.CategorizerResult;
import org.softwarecave.springbootnotecategorizer.categorizer.CategorizerResults;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
public class BedrockTextParser {

    public CategorizerResults parseResponse(byte[] byteArray) {
        var documentContext = JsonPath.parse(new String(byteArray));
        String responseText = documentContext.read("$.output.message.content[0].text");
        if (responseText == null) {
            throw new CategorizationException("Failed to categorize the note because the response text is null.");
        }
        return parseResponseText(responseText);
    }

    private CategorizerResults parseResponseText(String responseText) {
        var parts = responseText.trim().split("\\s+");

        List<CategorizerResult> results = Arrays.stream(parts)
                .map(this::parseSingleResult)
                .filter(Objects::nonNull)
                .toList();
        return new CategorizerResults(results);
    }

    private CategorizerResult parseSingleResult(String part) {
        var categoryAndScore = part.split("=");
        if (categoryAndScore.length != 2) {
            log.warn("Skipping invalid category and score pair: {}", part);
            return null;
        }
        var categoryName = categoryAndScore[0].trim();
        var scoreStr = categoryAndScore[1].trim();

        Category category = Category.valueOf(categoryName);
        if (category == null) {
            log.warn("Skipping invalid category: {}", categoryName);
            return null;
        }
        double score;
        try {
            score = Double.parseDouble(scoreStr);
        } catch (NumberFormatException e) {
            log.warn("Skipping invalid score value: {} for category: {}", scoreStr, category);
            return null;
        }

        return new CategorizerResult(category, score);
    }

}
