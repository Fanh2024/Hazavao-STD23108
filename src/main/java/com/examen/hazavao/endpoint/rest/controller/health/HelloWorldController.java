package com.examen.hazavao.endpoint.rest.controller;

import com.examen.hazavao.service.OpenAiService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class HelloWorldController {

    private final OpenAiService openAiService;

    @GetMapping("/hazavao")
    @SneakyThrows
    public String hazavao(@RequestParam String teny) {
        return openAiService.getDefinition(teny);
    }
}
