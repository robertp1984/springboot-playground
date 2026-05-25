package org.softwarecave.springbootnotecategorizer.categorizer.keywordbased;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.softwarecave.springbootnotecategorizer.Category;
import org.softwarecave.springbootnotecategorizer.categorizer.CategorizationException;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class KeywordMatrix {

    public record KeywordEntry(String keyword,
                               double weight,
                               MatchType matchType) {
    }

    public record CategoryEntry(Category category, List<KeywordEntry> keywords) {
    }

    private final Map<Category, List<KeywordEntry>> categoryEntries;

    public double getScore(Category category, String text) {
        var keywords = categoryEntries.get(category);
        if (keywords == null) {
            throw new CategorizationException("Category not found: " + category);
        }

        // TODO: optimize by using a more efficient data structure for keyword lookup
        String textLower = text.toLowerCase();
        double score = 0.0;
        for (var keywordEntry : keywords) {
            if (textLower.contains(keywordEntry.keyword().toLowerCase())) {
                score += keywordEntry.weight();
            }
        }

        long shouldMatchCount = keywords.stream().filter(k -> k.matchType() == MatchType.SHOULD).count();
        return score / shouldMatchCount;
    }

    public Set<Category> getCategories() {
        return categoryEntries.keySet();
    }

}
