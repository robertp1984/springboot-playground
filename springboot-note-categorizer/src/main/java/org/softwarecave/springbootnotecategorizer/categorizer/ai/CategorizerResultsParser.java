package org.softwarecave.springbootnotecategorizer.categorizer.ai;

import lombok.extern.slf4j.Slf4j;
import org.softwarecave.springbootnotecategorizer.Category;
import org.softwarecave.springbootnotecategorizer.categorizer.CategorizerResult;
import org.softwarecave.springbootnotecategorizer.categorizer.CategorizerResults;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
public class CategorizerResultsParser {

    private static CategorizerResult parseSingleResult(String part) {
        var categoryAndScore = part.split("=");
        if (categoryAndScore.length != 2) {
            log.warn("Skipping invalid category and score pair: {}", part);
            return null;
        }
        var categoryName = categoryAndScore[0].trim();
        var scoreStr = categoryAndScore[1].trim();

        Category category;
        try {
            category = Category.valueOf(categoryName);
        } catch (IllegalArgumentException ex) {
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

    public static CategorizerResults fromText(String responseText) {
        var parts = responseText.trim().split("\\s+");

        List<CategorizerResult> results = Arrays.stream(parts)
                .map(CategorizerResultsParser::parseSingleResult)
                .filter(Objects::nonNull)
                .toList();
        return new CategorizerResults(results);
    }
}
