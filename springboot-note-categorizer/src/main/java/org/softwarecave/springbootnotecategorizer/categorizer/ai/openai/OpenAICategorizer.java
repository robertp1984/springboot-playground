package org.softwarecave.springbootnotecategorizer.categorizer.ai.openai;

import lombok.extern.slf4j.Slf4j;
import org.softwarecave.springbootnotecategorizer.Category;
import org.softwarecave.springbootnotecategorizer.categorizer.CategorizationException;
import org.softwarecave.springbootnotecategorizer.categorizer.Categorizer;
import org.softwarecave.springbootnotecategorizer.categorizer.CategorizerResults;
import org.softwarecave.springbootnotecategorizer.categorizer.ai.CategorizerResultsParser;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class OpenAICategorizer implements Categorizer {

    private final ChatClient chatClient;
    private final String allCategoriesString;

    public OpenAICategorizer(OpenAiChatModel chatModel) {
        chatClient = ChatClient.builder(chatModel).build();
        allCategoriesString = Arrays.stream(Category.values())
                .map(Category::name)
                .collect(Collectors.joining(", "));
    }

    @Override
    public CategorizerResults categorize(String title, String body) {
        var systemPrompt = """
                You return a list of categories with scores in range 0-10 for a given note. The categories are %s.
                The response format is categoryName=score separated by spaces. Do not add anything else to the response."""
                .formatted(allCategoriesString);
        var userPrompt = "Please categorize the note with title: '%s' and body: '%s'."
                .formatted(title, body);

        String response;
        try {
            response = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .content();

            log.info("Returned response content " + response);
        } catch (Exception e) {
            log.error("Failed to categorize the note with title {} {} ", title, e.getMessage(), e);
            throw new CategorizationException("Could not categorize the note with title '%s'".formatted(title), e);
        }

        if (response != null) {
            return CategorizerResultsParser.fromText(response);
        } else {
            throw new CategorizationException("Failed to categorize the note because the response text is null.");
        }
    }
}
