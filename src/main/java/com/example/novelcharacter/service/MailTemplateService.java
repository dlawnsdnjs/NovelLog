package com.example.novelcharacter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailTemplateService {
    private final ResourceLoader resourceLoader;

    public String loadTemplate(String path, Map<String, String> bindings) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:templates/" + path);
        String html = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        // ${변수명} 치환
        for (var e : bindings.entrySet()) {
            html = html.replace("${" + e.getKey() + "}", e.getValue());
        }

        return html;
    }
}
