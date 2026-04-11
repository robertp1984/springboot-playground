package org.softwarecave.springbootnotecategorizer.categorizer.keywordbased;

import org.softwarecave.springbootnotecategorizer.categorizer.Categorizer;
import org.softwarecave.springbootnotecategorizer.categorizer.CategorizerResults;

public class SimpleKeywordBasedCategorizer implements Categorizer {

    private final KeywordMatrix keywordMatrix;

    public SimpleKeywordBasedCategorizer(KeywordMatrix keywordMatrix) {
        this.keywordMatrix = keywordMatrix;
    }

    @Override
    public CategorizerResults categorize(String title, String body) {
        CategorizerResults results = new CategorizerResults();

        for (var category : keywordMatrix.getCategories()) {
            double titleScore = keywordMatrix.getScore(category, title);
            double bodyScore = keywordMatrix.getScore(category, body);
            double totalScore = titleScore + bodyScore;
            results.addResult(category, totalScore);
        }

        return results;
    }
}
