package com.example.dialoguedemo.dm.state;

import com.example.dialoguedemo.model.ConversationContext;
import com.example.dialoguedemo.nlu.model.NluResult;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * DST：将 NLU 结果写入上下文，供策略层和 NLG 使用。
 */
@Component
public class DialogueStateTracker {

    public void apply(ConversationContext context, NluResult result) {
        context.incrementTurn();
        context.getSlots().put("lastUpdated", Instant.now().toString());
        if (result == null || result.isFallback()) {
            return;
        }
        if (result.getDomain() != null) {
            context.setDomain(result.getDomain());
        }
        if (result.getIntent() != null) {
            context.setLastIntent(result.getIntent().getId());
        }
        result.getSlots().forEach(context.getSlots()::put);
    }
}
