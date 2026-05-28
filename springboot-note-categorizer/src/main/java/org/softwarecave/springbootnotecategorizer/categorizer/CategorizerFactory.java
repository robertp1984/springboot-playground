package org.softwarecave.springbootnotecategorizer.categorizer;

import org.softwarecave.springbootnotecategorizer.categorizer.ai.bedrock.BedrockCategorizer;
import org.softwarecave.springbootnotecategorizer.categorizer.ai.openai.OpenAICategorizer;
import org.softwarecave.springbootnotecategorizer.categorizer.keywords.KeywordMatrix;
import org.softwarecave.springbootnotecategorizer.categorizer.keywords.KeywordMatrixLoader;
import org.softwarecave.springbootnotecategorizer.categorizer.keywords.SimpleKeywordsCategorizer;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

@Service
public class CategorizerFactory {

    public static final String DEFAULT_KEYWORD_MATRIX_FILENAME = "keyword-matrix.json";

    private final JsonMapper jsonMapper;
    private final ChatClient.Builder chatClientBuilder;
    private final CategorizerEngine categorizerEngine;

    public CategorizerFactory(JsonMapper jsonMapper, ChatClient.Builder chatClientBuilder,
                              @Value("${app.categorizer.engine}") CategorizerEngine categorizerEngine) {
        this.jsonMapper = jsonMapper;
        this.chatClientBuilder = chatClientBuilder;
        this.categorizerEngine = categorizerEngine;
    }

    public Categorizer getKeywordBasedCategorizer() {
        KeywordMatrix keywordMatrix = KeywordMatrixLoader.fromFile(DEFAULT_KEYWORD_MATRIX_FILENAME, jsonMapper);
        return new SimpleKeywordsCategorizer(keywordMatrix);
    }

    public Categorizer getBedrockBasedCategorizer() {
        return new BedrockCategorizer(jsonMapper);
    }

    public Categorizer getOpenAICategorizer() {
        return new OpenAICategorizer(chatClientBuilder);
    }

    public Categorizer getDefaultCategorizer() {
        return switch (categorizerEngine) {
            case KEYWORDS -> getKeywordBasedCategorizer();
            case BEDROCK -> getBedrockBasedCategorizer();
            case OPENAI -> getOpenAICategorizer();
        };
    }
}
