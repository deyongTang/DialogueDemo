package com.example.dialoguedemo.model;

import java.util.HashMap;
import java.util.Map;

public class ConversationContext {
    private final String sessionId;
    private String lastIntent;
    private final Map<String, Object> slots = new HashMap<>();

    public ConversationContext(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getLastIntent() {
        return lastIntent;
    }

    public void setLastIntent(String lastIntent) {
        this.lastIntent = lastIntent;
    }

    public Map<String, Object> getSlots() {
        return slots;
    }
}
