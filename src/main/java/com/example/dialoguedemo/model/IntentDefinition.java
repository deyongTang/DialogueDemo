package com.example.dialoguedemo.model;

import java.util.List;

public class IntentDefinition {
    private String id;
    private List<String> keywords;
    private String replyTemplate;
    private ActionType actionType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getReplyTemplate() {
        return replyTemplate;
    }

    public void setReplyTemplate(String replyTemplate) {
        this.replyTemplate = replyTemplate;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }
}
