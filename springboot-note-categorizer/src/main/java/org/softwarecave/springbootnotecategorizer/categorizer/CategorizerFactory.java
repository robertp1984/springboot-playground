package org.softwarecave.springbootnotecategorizer.categorizer;

import lombok.RequiredArgsConstructor;
import org.softwarecave.springbootnotecategorizer.categorizer.aibased.BedrockBasedCategorizer;
import org.softwarecave.springbootnotecategorizer.categorizer.keywordbased.KeywordMatrix;
import org.softwarecave.springbootnotecategorizer.categorizer.keywordbased.KeywordMatrixLoader;
import org.softwarecave.springbootnotecategorizer.categorizer.keywordbased.SimpleKeywordBasedCategorizer;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

@Service
@RequiredArgsConstructor
public class CategorizerFactory {

    public static final String DEFAULT_KEYWORD_MATRIX_FILENAME = "keyword-matrix.json";

    private final JsonMapper jsonMapper;

    public Categorizer getKeywordBasedCategorizer() {
        KeywordMatrix keywordMatrix = KeywordMatrixLoader.fromFile(DEFAULT_KEYWORD_MATRIX_FILENAME, jsonMapper);
        return new SimpleKeywordBasedCategorizer(keywordMatrix);
    }

    public Categorizer getBedrockBasedCategorizer() {
        return new BedrockBasedCategorizer(jsonMapper);
    }
}
