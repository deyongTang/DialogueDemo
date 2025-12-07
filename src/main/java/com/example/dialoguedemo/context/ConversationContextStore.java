package com.example.dialoguedemo.context;

import com.example.dialoguedemo.model.ConversationContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConversationContextStore {

    private final Map<String, ConversationContext> contexts = new ConcurrentHashMap<>();

    public ConversationContext getOrCreate(String sessionId) {
        return contexts.computeIfAbsent(sessionId, ConversationContext::new);
    }

    public ConversationContext get(String sessionId) {
        return contexts.get(sessionId);
    }
}
