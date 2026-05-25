package org.softwarecave.springbootnotecategorizer;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
public class SpringBootNoteCategorizerApp {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(SpringBootNoteCategorizerApp.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(StickyNoteCategorizingProcessor processor) {
        return (args) -> processor.run();
    }

}
