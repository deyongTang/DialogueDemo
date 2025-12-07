package com.example.dialoguedemo.dm.policy;

import com.example.dialoguedemo.model.ConversationContext;
import com.example.dialoguedemo.model.IntentDefinition;
import com.example.dialoguedemo.model.SlotDefinition;
import com.example.dialoguedemo.nlu.model.NluResult;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * DPL：根据上下文判断是否需要补槽或直接进入业务回复。
 */
@Component
public class DialoguePolicy {

    public PolicyDecision decide(ConversationContext context, NluResult result) {
        if (result == null || result.isFallback() || result.getIntent() == null) {
            return PolicyDecision.fallback();
        }

        IntentDefinition intent = result.getIntent();
        Optional<SlotDefinition> missingSlot = intent.getSlots().stream()
                .filter(SlotDefinition::isRequired)
                .filter(slot -> !context.getSlots().containsKey(slot.getName()))
                .findFirst();

        if (missingSlot.isPresent()) {
            context.setPendingSlot(missingSlot.get().getName());
            return PolicyDecision.missingSlot(intent, missingSlot.get());
        }
        context.setPendingSlot(null);
        return PolicyDecision.resolved(intent);
    }
}
