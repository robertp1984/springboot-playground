package org.softwarecave.springbootnote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringbootNoteApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootNoteApplication.class, args);
    }

}
