package org.softwarecave.springbootnotecategorizer.categorizer;

import org.softwarecave.springbootnotecategorizer.Category;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class CategorizerResults {
    private final List<CategorizerResult> results;

    public CategorizerResults() {
        this.results = new ArrayList<>();
    }

    public CategorizerResults(Collection<CategorizerResult> results) {
        this.results = new ArrayList<>(results);
    }

    public void addResult(Category category, double score) {
        this.results.add(new CategorizerResult(category, score));
    }

    public List<CategorizerResult> getTopKResults(int k) {
        return results.stream()
                .sorted(Comparator.comparingDouble(CategorizerResult::getScore).reversed())
                .limit(k)
                .toList();
    }
}
