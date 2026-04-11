package org.softwarecave.springbootnote.hello;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "/api/v1")
public class HelloController {

    @GetMapping(path = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
    public HelloDTO getHello() {
        return new HelloDTO("Good Morning my friend.", LocalDateTime.now());
    }
}
