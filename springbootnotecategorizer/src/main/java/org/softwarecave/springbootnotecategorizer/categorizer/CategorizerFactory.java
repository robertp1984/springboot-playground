package org.softwarecave.springbootnotecategorizer.categorizer;

import org.softwarecave.springbootnotecategorizer.categorizer.aibased.BedrockBasedCategorizer;
import org.softwarecave.springbootnotecategorizer.categorizer.keywordbased.KeywordMatrix;
import org.softwarecave.springbootnotecategorizer.categorizer.keywordbased.SimpleKeywordBasedCategorizer;

public class CategorizerFactory {

    public static final String DEFAULT_KEYWORD_MATRIX_FILENAME = "keyword-matrix.json";

    public Categorizer getKeywordBasedCategorizer() {
        KeywordMatrix keywordMatrix = new KeywordMatrix();
        keywordMatrix.loadFromFile(DEFAULT_KEYWORD_MATRIX_FILENAME);
        return new SimpleKeywordBasedCategorizer(keywordMatrix);
    }

    public Categorizer getBedrockBasedCategorizer() {
        return new BedrockBasedCategorizer();
    }
}
