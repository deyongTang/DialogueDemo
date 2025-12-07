package com.example.dialoguedemo.dm;

import com.example.dialoguedemo.context.ConversationContextStore;
import com.example.dialoguedemo.dm.policy.DialoguePolicy;
import com.example.dialoguedemo.dm.policy.PolicyDecision;
import com.example.dialoguedemo.dm.state.DialogueStateTracker;
import com.example.dialoguedemo.model.ConversationContext;
import com.example.dialoguedemo.model.DialogueRequest;
import com.example.dialoguedemo.model.DialogueResponse;
import com.example.dialoguedemo.nlg.TemplateNlgService;
import com.example.dialoguedemo.nlu.IntentMatcher;
import com.example.dialoguedemo.nlu.model.NluResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 管线协调者：串联 NLU -> DST -> DPL -> NLG 并返回最终回复。
 */
@Service
public class DialogueManager {

    private static final Logger log = LoggerFactory.getLogger(DialogueManager.class);

    private final IntentMatcher intentMatcher;
    private final ConversationContextStore contextStore;
    private final DialogueStateTracker stateTracker;
    private final DialoguePolicy dialoguePolicy;
    private final TemplateNlgService nlgService;

    public DialogueManager(IntentMatcher intentMatcher,
                           ConversationContextStore contextStore,
                           DialogueStateTracker stateTracker,
                           DialoguePolicy dialoguePolicy,
                           TemplateNlgService nlgService) {
        this.intentMatcher = intentMatcher;
        this.contextStore = contextStore;
        this.stateTracker = stateTracker;
        this.dialoguePolicy = dialoguePolicy;
        this.nlgService = nlgService;
    }

    /**
     * 执行完整对话流程，并返回包含 domain/intent 的响应。
     */
    public DialogueResponse handleMessage(DialogueRequest request) {
        long start = System.currentTimeMillis();
        ConversationContext context = contextStore.getOrCreate(request.getSessionId());
        NluResult nluResult = intentMatcher.analyze(request.getMessage());
        nluResult = recoverPendingSlot(context, request.getMessage(), nluResult);
        stateTracker.apply(context, nluResult);
        PolicyDecision decision = dialoguePolicy.decide(context, nluResult);
        String reply = nlgService.render(decision, context);
        String intentId = decision.getIntentId();

        long cost = System.currentTimeMillis() - start;
        log.info("session={}, domain={}, intent={}, cost={}ms", request.getSessionId(), context.getDomain(), intentId, cost);
        return new DialogueResponse(request.getSessionId(), context.getDomain(), reply, intentId, new HashMap<>(context.getSlots()));
    }

    private NluResult recoverPendingSlot(ConversationContext context, String message, NluResult nluResult) {
        if (!nluResult.isFallback()) {
            return nluResult;
        }
        String pendingSlot = context.getPendingSlot();
        String lastIntentId = context.getLastIntent();
        if (pendingSlot == null || lastIntentId == null) {
            return nluResult;
        }
        return intentMatcher.findIntentById(lastIntentId)
                .map(intent -> {
                    Map<String, String> slots = intentMatcher.extractSlots(intent, message);
                    String value = slots.get(pendingSlot);
                    if (value == null || value.isBlank()) {
                        value = message.trim();
                    }
                    if (value.isEmpty()) {
                        return nluResult;
                    }
                    Map<String, String> merged = new HashMap<>();
                    merged.put(pendingSlot, value);
                    return NluResult.of(intent, merged);
                })
                .orElse(nluResult);
    }
}
