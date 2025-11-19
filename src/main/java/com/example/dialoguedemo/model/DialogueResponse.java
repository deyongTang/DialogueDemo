package com.example.dialoguedemo.model;

import java.util.Map;

public class DialogueResponse {
    private String sessionId;
    private String domain;
    private String reply;
    private String intentId;
    private Map<String, Object> context;

    public DialogueResponse(String sessionId,
                             String domain,
                             String reply,
                             String intentId,
                             Map<String, Object> context) {
        this.sessionId = sessionId;
        this.domain = domain;
        this.reply = reply;
        this.intentId = intentId;
        this.context = context;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getDomain() {
        return domain;
    }

    public String getReply() {
        return reply;
    }

    public String getIntentId() {
        return intentId;
    }

    public Map<String, Object> getContext() {
        return context;
    }
}
