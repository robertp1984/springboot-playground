package org.softwarecave.springbootnotecategorizer.categorizer.aibased;

import lombok.extern.slf4j.Slf4j;
import org.softwarecave.springbootnotecategorizer.Category;
import org.softwarecave.springbootnotecategorizer.categorizer.CategorizationException;
import org.softwarecave.springbootnotecategorizer.categorizer.Categorizer;
import org.softwarecave.springbootnotecategorizer.categorizer.CategorizerResults;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
public class BedrockBasedCategorizer implements Categorizer {

    private static final String TEXT_GEN_MODEL = "amazon.nova-pro-v1:0";
    private static final String APPLICATION_JSON = "application/json";
    private static final double TEMPERATURE = 0.7;

    private final JsonMapper jsonMapper;
    private final String allCategoriesString;

    public BedrockBasedCategorizer() {
        jsonMapper = new JsonMapper();
        allCategoriesString = Arrays.stream(Category.values())
                .map(Category::name)
                .collect(Collectors.joining(", "));
    }

    @Override
    public CategorizerResults categorize(String title, String body) {
        String jsonRequest = createRequest(title, body);
        log.info("jsonRequest={}", jsonRequest);

        try (BedrockRuntimeClient client = createClient()) {

            var response = client.invokeModel(request -> request.body(SdkBytes.fromUtf8String(jsonRequest))
                    .modelId(TEXT_GEN_MODEL)
                    .accept(APPLICATION_JSON));

            return new BedrockTextParser().parseResponse(response.body().asByteArray());
        } catch (Exception e) {
            log.error("Failed to categorize the note with title {} {} ", title, e.getMessage(), e);
            throw new CategorizationException("Could not categorize the note with title '%s'".formatted(title), e);
        }
    }

    private String createRequest(String title, String body) {
        var systemPrompt = "You return a list of categories with scores in range 0-10 for a given note. The categories are %s. The response format is categoryName=score separated by spaces. Do not add anything else to the response."
                .formatted(allCategoriesString);
        var userPrompt = "Please categorize the note with title: '%s' and body: '%s'."
                .formatted(title, body);

        ObjectNode root = jsonMapper.createObjectNode();
        root.putArray("system")
                .addObject().put("text", systemPrompt);
        root.putArray("messages")
                .addObject()
                .put("role", "user")
                .putArray("content")
                .addObject()
                .put("text", userPrompt);
        root.putObject("inferenceConfig")
                .put("maxTokens", 300)
                .put("temperature", TEMPERATURE);

        return jsonMapper.writeValueAsString(root);
    }


    private BedrockRuntimeClient createClient() {
        return BedrockRuntimeClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .region(Region.US_EAST_1)
                .build();
    }

}
