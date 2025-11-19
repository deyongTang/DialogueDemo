package com.example.dialoguedemo.model;

import java.util.HashMap;
import java.util.Map;

public class ConversationContext {
    private final String sessionId;
    private String lastIntent;
    private String domain;
    private int turnNumber;
    private String pendingSlot;
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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    /**
     * DM 每处理一轮对话都会自增回合数，用于多轮指标。
     */
    public void incrementTurn() {
        this.turnNumber++;
    }

    public String getPendingSlot() {
        return pendingSlot;
    }

    public void setPendingSlot(String pendingSlot) {
        this.pendingSlot = pendingSlot;
    }

    public Map<String, Object> getSlots() {
        return slots;
    }
}
