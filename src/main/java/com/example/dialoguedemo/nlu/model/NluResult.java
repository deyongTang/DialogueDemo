package com.example.dialoguedemo.nlu.model;

import com.example.dialoguedemo.model.IntentDefinition;

import java.util.Collections;
import java.util.Map;

public class NluResult {

    private final IntentDefinition intent;
    private final String domain;
    private final Map<String, String> slots;
    private final boolean fallback;

    private NluResult(IntentDefinition intent,
                      String domain,
                      Map<String, String> slots,
                      boolean fallback) {
        this.intent = intent;
        this.domain = domain;
        this.slots = slots == null ? Collections.emptyMap() : slots;
        this.fallback = fallback;
    }

    public static NluResult of(IntentDefinition intent, Map<String, String> slots) {
        return new NluResult(intent, intent != null ? intent.getDomain() : null, slots, false);
    }

    public static NluResult fallback() {
        return new NluResult(null, null, Collections.emptyMap(), true);
    }

    public IntentDefinition getIntent() {
        return intent;
    }

    public String getDomain() {
        return domain;
    }

    public Map<String, String> getSlots() {
        return slots;
    }

    public boolean isFallback() {
        return fallback;
    }
}
