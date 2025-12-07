package com.example.dialoguedemo.nlu.config;

import com.example.dialoguedemo.model.ActionType;
import com.example.dialoguedemo.model.IntentDefinition;
import com.example.dialoguedemo.model.SlotDefinition;
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
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 从 Rasa 风格的 nlu.yml 中解析领域、意图、槽位等配置。
 */
@Component
public class IntentConfigLoader {

    private static final Logger log = LoggerFactory.getLogger(IntentConfigLoader.class);
    private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
    private List<IntentDefinition> intents = Collections.emptyList();

    @PostConstruct
    public void loadConfig() throws IOException {
        ClassPathResource resource = new ClassPathResource("intents.yml");
        try (InputStream inputStream = resource.getInputStream()) {
            RasaNluConfig rasaConfig = objectMapper.readValue(inputStream, RasaNluConfig.class);
            if (rasaConfig == null || rasaConfig.getNlu() == null) {
                log.warn("No Rasa NLU config found in intents.yml");
                intents = Collections.emptyList();
                return;
            }

            intents = rasaConfig.getNlu().stream()
                    .map(this::toIntentDefinition)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            log.info("Loaded {} intent definitions", intents.size());
        }
    }

    private IntentDefinition toIntentDefinition(RasaIntentEntry entry) {
        if (entry.getIntent() == null) {
            log.warn("Skip intent entry without name: {}", entry);
            return null;
        }
        IntentDefinition definition = new IntentDefinition();
        definition.setId(entry.getIntent());
        definition.setExamples(parseExamples(entry.getExamples()));

        RasaIntentMetadata metadata = entry.getMetadata();
        ActionType actionType = metadata != null && metadata.getActionType() != null
                ? metadata.getActionType()
                : ActionType.FAQ;
        definition.setActionType(actionType);
        if (metadata != null) {
            definition.setReplyTemplate(metadata.getReplyTemplate());
            definition.setDomain(metadata.getDomain());
            definition.setSkillName(metadata.getSkillName());
            definition.setSlots(parseSlots(metadata.getSlots()));
        }
        return definition;
    }

    /**
     * Rasa examples 字段使用 |- 块，逐行解析去掉 "-".
     */
    private List<String> parseExamples(String block) {
        if (block == null) {
            return Collections.emptyList();
        }
        return block.lines()
                .map(String::trim)
                .filter(line -> line.startsWith("-"))
                .map(line -> line.substring(1).trim())
                .filter(line -> !line.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 将 YAML 中的槽位转成运行时模型，保留 prompt/pattern 等信息。
     */
    private List<SlotDefinition> parseSlots(List<RasaSlotDefinition> slots) {
        if (slots == null || slots.isEmpty()) {
            return Collections.emptyList();
        }
        return slots.stream()
                .filter(slot -> slot.getName() != null)
                .map(slot -> {
                    SlotDefinition definition = new SlotDefinition();
                    definition.setName(slot.getName());
                    definition.setKeywords(slot.getKeywords());
                    definition.setPattern(slot.getPattern());
                    definition.setRequired(Boolean.TRUE.equals(slot.getRequired()));
                    definition.setPrompt(slot.getPrompt());
                    return definition;
                })
                .collect(Collectors.toList());
    }

    public List<IntentDefinition> getIntents() {
        return intents;
    }

    /**
     * Minimal POJOs to read a subset of the Rasa NLU YAML schema.
     */
    public static class RasaNluConfig {
        private String version;
        private List<RasaIntentEntry> nlu;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public List<RasaIntentEntry> getNlu() {
            return nlu;
        }

        public void setNlu(List<RasaIntentEntry> nlu) {
            this.nlu = nlu;
        }
    }

    public static class RasaIntentEntry {
        private String intent;
        private String examples;
        private RasaIntentMetadata metadata;

        public String getIntent() {
            return intent;
        }

        public void setIntent(String intent) {
            this.intent = intent;
        }

        public String getExamples() {
            return examples;
        }

        public void setExamples(String examples) {
            this.examples = examples;
        }

        public RasaIntentMetadata getMetadata() {
            return metadata;
        }

        public void setMetadata(RasaIntentMetadata metadata) {
            this.metadata = metadata;
        }

        @Override
        public String toString() {
            return "RasaIntentEntry{" +
                    "intent='" + intent + '\'' +
                    '}';
        }
    }

    public static class RasaIntentMetadata {
        private ActionType actionType = ActionType.FAQ;
        private String replyTemplate;
        private String domain;
        private List<RasaSlotDefinition> slots;
        private String skillName;

        public ActionType getActionType() {
            return actionType;
        }

        public void setActionType(ActionType actionType) {
            this.actionType = actionType;
        }

        public String getReplyTemplate() {
            return replyTemplate;
        }

        public void setReplyTemplate(String replyTemplate) {
            this.replyTemplate = replyTemplate;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public List<RasaSlotDefinition> getSlots() {
            return slots;
        }

        public void setSlots(List<RasaSlotDefinition> slots) {
            this.slots = slots;
        }

        public String getSkillName() {
            return skillName;
        }

        public void setSkillName(String skillName) {
            this.skillName = skillName;
        }
    }

    public static class RasaSlotDefinition {
        private String name;
        private List<String> keywords = Collections.emptyList();
        private String pattern;
        private Boolean required;
        private String prompt;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getKeywords() {
            return keywords;
        }

        public void setKeywords(List<String> keywords) {
            this.keywords = keywords;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public Boolean getRequired() {
            return required;
        }

        public void setRequired(Boolean required) {
            this.required = required;
        }

        public String getPrompt() {
            return prompt;
        }

        public void setPrompt(String prompt) {
            this.prompt = prompt;
        }
    }
}
