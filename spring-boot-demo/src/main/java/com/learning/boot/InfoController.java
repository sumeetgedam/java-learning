package com.learning.boot;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {

    private final AppProperties properties;

    public InfoController(
            AppProperties properties
    ) {
        this.properties = properties;
    }

    @GetMapping("/info")
    public String info() {
        return properties.getName() + " : " + properties.getGreeting();
    }
}
