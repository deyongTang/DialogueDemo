package com.example.dialoguedemo.config;

import com.example.dialoguedemo.model.IntentDefinition;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Component
public class IntentConfigLoader {

    private static final Logger log = LoggerFactory.getLogger(IntentConfigLoader.class);
    private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
    private List<IntentDefinition> intents = Collections.emptyList();

    @PostConstruct
    public void loadConfig() throws IOException {
        ClassPathResource resource = new ClassPathResource("intents.yml");
        try (InputStream inputStream = resource.getInputStream()) {
            intents = objectMapper.readValue(inputStream, new TypeReference<>() {
            });
            log.info("Loaded {} intent definitions", intents.size());
        }
    }

    public List<IntentDefinition> getIntents() {
        return intents;
    }
}
