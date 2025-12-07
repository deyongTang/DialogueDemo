package com.example.dialoguedemo.dm.policy;

import com.example.dialoguedemo.model.IntentDefinition;
import com.example.dialoguedemo.model.SlotDefinition;

public class PolicyDecision {

    private final IntentDefinition intent;
    private final SlotDefinition pendingSlot;
    private final boolean fallback;

    private PolicyDecision(IntentDefinition intent,
                           SlotDefinition pendingSlot,
                           boolean fallback) {
        this.intent = intent;
        this.pendingSlot = pendingSlot;
        this.fallback = fallback;
    }

    public static PolicyDecision fallback() {
        return new PolicyDecision(null, null, true);
    }

    public static PolicyDecision missingSlot(IntentDefinition intent, SlotDefinition pendingSlot) {
        return new PolicyDecision(intent, pendingSlot, false);
    }

    public static PolicyDecision resolved(IntentDefinition intent) {
        return new PolicyDecision(intent, null, false);
    }

    public IntentDefinition getIntent() {
        return intent;
    }

    public SlotDefinition getPendingSlot() {
        return pendingSlot;
    }

    public boolean isFallback() {
        return fallback;
    }

    public String getIntentId() {
        if (intent != null) {
            return intent.getId();
        }
        return "fallback";
    }

    public boolean requiresSlotPrompt() {
        return pendingSlot != null;
    }
}
