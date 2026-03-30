package org.softwarecave.springbootnotecategorizer.categorizer.keywordbased;

import lombok.extern.slf4j.Slf4j;
import org.softwarecave.springbootnotecategorizer.Category;
import org.softwarecave.springbootnotecategorizer.categorizer.CategorizationException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class KeywordMatrix {

    public record KeywordEntry(String keyword,
                               double weight,
                               MatchType matchType) {
    }

    public record CategoryEntry(Category category, List<KeywordEntry> keywords) {
    }


    private Map<Category, List<KeywordEntry>> categoryEntries;

    public void loadFromFile(String filename) {
        try (var is = getClass().getClassLoader().getResourceAsStream(filename)) {
            if (is == null) {
                throw new CategorizationException("File not found: " + filename);
            }

            JsonMapper jsonMapper = new JsonMapper();
            List<CategoryEntry> categoryEntryList = jsonMapper.readValue(is, new TypeReference<List<CategoryEntry>>() {
            });
            categoryEntries = categoryEntryList.stream()
                    .collect(Collectors.toMap(CategoryEntry::category, CategoryEntry::keywords));
            log.info("Loaded {} categories", categoryEntries);
        } catch (IOException e) {
            throw new CategorizationException("Failed to load keyword matrix from file: " + filename, e);
        }
    }

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
