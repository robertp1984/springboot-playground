package org.softwarecave.springbootnotecategorizer;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

    public static final String BOOTSTRAP_SERVERS = "localhost:29092";
    public static final String INPUT_TOPIC = "playground.stickynote";
    public static final String OUTPUT_TOPIC = "playground.stickynote.categorized";

    public static void main(String[] args) throws InterruptedException {
        ParsedArgs parsedArgs = parseArgs(args);
        if (parsedArgs == null) {
            return;
        }

        StickyNoteCategorizerApp app = new StickyNoteCategorizerApp(parsedArgs.bootstrapServers(), parsedArgs.inputTopic(),
                parsedArgs.outputTopic());
        app.run();
    }

    private record ParsedArgs(String bootstrapServers, String inputTopic, String outputTopic) {
    }

    private static ParsedArgs parseArgs(String[] args) {
        ParsedArgs result;
        if (args.length == 0) {
            result = new ParsedArgs(BOOTSTRAP_SERVERS, INPUT_TOPIC, OUTPUT_TOPIC);
        } else if (args.length == 3) {
            result = new ParsedArgs(args[0], args[1], args[2]);
        } else {
            log.error("""
                    Invalid number of arguments.
                    Usage: Main <bootstrapServers> <inputTopic> <outputTopic>
                    """);
            return null;
        }
        log.info("Parsed arguments: {}", result);
        return result;
    }
}
