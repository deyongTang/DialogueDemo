package com.example.dialoguedemo.nlu;

import com.example.dialoguedemo.model.IntentDefinition;
import com.example.dialoguedemo.model.SlotDefinition;
import com.example.dialoguedemo.nlu.config.IntentConfigLoader;
import com.example.dialoguedemo.nlu.model.NluResult;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * NLU 模块：使用配置中的示例语句做朴素匹配，同时完成槽位抽取。
 */
@Component
public class IntentMatcher {

    private final IntentConfigLoader intentConfigLoader;

    public IntentMatcher(IntentConfigLoader intentConfigLoader) {
        this.intentConfigLoader = intentConfigLoader;
    }

    /**
     * 解析用户输入，返回领域/意图/槽位等基础理解结果。
     */
    public NluResult analyze(String message) {
        if (message == null || message.isBlank()) {
            return NluResult.fallback();
        }
        String lowerMessage = message.toLowerCase(Locale.ROOT);
        List<IntentDefinition> intents = intentConfigLoader.getIntents();
        Optional<IntentDefinition> definition = intents.stream()
                .filter(intent -> intent.getExamples().stream()
                        .anyMatch(example -> lowerMessage.contains(example.toLowerCase(Locale.ROOT))))
                .findFirst();

        if (definition.isEmpty()) {
            return NluResult.fallback();
        }
        IntentDefinition intent = definition.get();
        Map<String, String> extracted = extractSlots(intent, message);
        return NluResult.of(intent, extracted);
    }

    public Optional<IntentDefinition> findIntentById(String intentId) {
        if (intentId == null) {
            return Optional.empty();
        }
        return intentConfigLoader.getIntents().stream()
                .filter(intent -> intentId.equals(intent.getId()))
                .findFirst();
    }

    /**
     * 根据 SlotDefinition 定义提取槽位，先尝试正则再回退到关键词。
     */
    public Map<String, String> extractSlots(IntentDefinition intent, String message) {
        return doExtractSlots(intent, message);
    }

    private Map<String, String> doExtractSlots(IntentDefinition intent, String message) {
        Map<String, String> slots = new HashMap<>();
        if (intent.getSlots().isEmpty()) {
            return slots;
        }
        String lowerMessage = message.toLowerCase(Locale.ROOT);
        for (SlotDefinition definition : intent.getSlots()) {
            String value = null;
            if (definition.getPattern() != null && !definition.getPattern().isEmpty()) {
                value = extractByPattern(definition.getPattern(), message);
            }
            if (value == null && !definition.getKeywords().isEmpty()) {
                value = definition.getKeywords().stream()
                        .filter(keyword -> lowerMessage.contains(keyword.toLowerCase(Locale.ROOT)))
                        .findFirst()
                        .orElse(null);
            }
            if (value != null) {
                slots.put(definition.getName(), value);
            }
        }
        return slots;
    }

    private String extractByPattern(String patternText, String message) {
        try {
            Pattern pattern = Pattern.compile(patternText, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                if (matcher.groupCount() >= 1) {
                    return matcher.group(1);
                }
                return matcher.group();
            }
        } catch (Exception ignored) {
            // ignore malformed pattern and fallback to keyword strategy
        }
        return null;
    }
}
