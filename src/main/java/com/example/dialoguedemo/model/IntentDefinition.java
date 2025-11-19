package com.example.dialoguedemo.model;

import java.util.Collections;
import java.util.List;

public class IntentDefinition {
    private String id;
    private String domain;
    private List<String> examples = Collections.emptyList();
    private String replyTemplate;
    private ActionType actionType;
    private List<SlotDefinition> slots = Collections.emptyList();
    private String skillName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public List<String> getExamples() {
        return examples;
    }

    public void setExamples(List<String> examples) {
        this.examples = examples == null ? Collections.emptyList() : examples;
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

    public List<SlotDefinition> getSlots() {
        return slots;
    }

    public void setSlots(List<SlotDefinition> slots) {
        this.slots = slots == null ? Collections.emptyList() : slots;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }
}
