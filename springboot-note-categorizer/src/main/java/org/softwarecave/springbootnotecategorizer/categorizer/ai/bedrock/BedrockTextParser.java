package org.softwarecave.springbootnotecategorizer.categorizer.ai.bedrock;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.softwarecave.springbootnotecategorizer.categorizer.CategorizationException;
import org.softwarecave.springbootnotecategorizer.categorizer.CategorizerResults;
import org.softwarecave.springbootnotecategorizer.categorizer.ai.CategorizerResultsParser;

@Slf4j
public class BedrockTextParser {

    public static CategorizerResults parseResponse(byte[] byteArray) {
        var documentContext = JsonPath.parse(new String(byteArray));
        String responseText = documentContext.read("$.output.message.content[0].text");
        if (responseText == null) {
            throw new CategorizationException("Failed to categorize the note because the response text is null.");
        }
        return CategorizerResultsParser.fromText(responseText);
    }

}
