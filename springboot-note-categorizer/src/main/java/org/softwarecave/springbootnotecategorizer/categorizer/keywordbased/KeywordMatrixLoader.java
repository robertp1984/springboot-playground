package org.softwarecave.springbootnotecategorizer.categorizer.keywordbased;

import lombok.extern.slf4j.Slf4j;
import org.softwarecave.springbootnotecategorizer.categorizer.CategorizationException;
import org.softwarecave.springbootnotecategorizer.categorizer.keywordbased.KeywordMatrix.CategoryEntry;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class KeywordMatrixLoader {

    public static KeywordMatrix fromFile(String filename, JsonMapper jsonMapper) {
        try (var is = KeywordMatrixLoader.class.getClassLoader().getResourceAsStream(filename)) {
            if (is == null) {
                throw new CategorizationException("File %s with keyword matrix was not found".formatted(filename));
            }

            List<CategoryEntry> categoryEntryList = jsonMapper.readValue(is, new TypeReference<List<CategoryEntry>>() {
            });
            var categoryMapping = categoryEntryList.stream()
                    .collect(Collectors.toMap(CategoryEntry::category, CategoryEntry::keywords));
            log.info("Loaded {} categories", categoryMapping);
            return new KeywordMatrix(categoryMapping);
        } catch (IOException e) {
            throw new CategorizationException("Failed to load keyword matrix from file %s".formatted(filename), e);
        }
    }

}
