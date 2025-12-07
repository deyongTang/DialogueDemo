package com.example.dialoguedemo.model;

import java.util.Collections;
import java.util.List;

public class SlotDefinition {
    private String name;
    private List<String> keywords = Collections.emptyList();
    private String pattern;
    private boolean required;
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
        this.keywords = keywords == null ? Collections.emptyList() : keywords;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
