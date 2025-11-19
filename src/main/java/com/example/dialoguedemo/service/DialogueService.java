package com.example.dialoguedemo.service;

import com.example.dialoguedemo.config.IntentConfigLoader;
import com.example.dialoguedemo.model.ActionType;
import com.example.dialoguedemo.model.ConversationContext;
import com.example.dialoguedemo.model.DialogueRequest;
import com.example.dialoguedemo.model.DialogueResponse;
import com.example.dialoguedemo.model.IntentDefinition;
import com.example.dialoguedemo.store.ConversationContextStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class DialogueService {

    private static final Logger log = LoggerFactory.getLogger(DialogueService.class);

    private final IntentConfigLoader intentConfigLoader;
    private final ConversationContextStore contextStore;

    public DialogueService(IntentConfigLoader intentConfigLoader,
                           ConversationContextStore contextStore) {
        this.intentConfigLoader = intentConfigLoader;
        this.contextStore = contextStore;
    }

    public DialogueResponse handleMessage(DialogueRequest request) {
        long start = System.currentTimeMillis();
        ConversationContext context = contextStore.getOrCreate(request.getSessionId());
        IntentDefinition matched = matchIntent(request.getMessage(), intentConfigLoader.getIntents());

        String reply;
        String intentId;
        if (matched == null) {
            reply = "抱歉没有理解您的意思，我们的客服稍后联系您。";
            intentId = "fallback";
        } else {
            intentId = matched.getId();
            reply = buildReply(matched, context);
            context.setLastIntent(intentId);
        }
        context.getSlots().put("lastUpdated", Instant.now().toString());

        long cost = System.currentTimeMillis() - start;
        log.info("session={}, intent={}, cost={}ms", request.getSessionId(), intentId, cost);
        return new DialogueResponse(request.getSessionId(), reply, intentId, new HashMap<>(context.getSlots()));
    }

    private IntentDefinition matchIntent(String message, List<IntentDefinition> intents) {
        if (message == null) {
            return null;
        }
        String lowerMessage = message.toLowerCase(Locale.ROOT);
        Optional<IntentDefinition> definition = intents.stream()
                .filter(intent -> intent.getKeywords().stream()
                        .anyMatch(keyword -> lowerMessage.contains(keyword.toLowerCase(Locale.ROOT))))
                .findFirst();
        return definition.orElse(null);
    }

    private String buildReply(IntentDefinition matched, ConversationContext context) {
        ActionType type = matched.getActionType();
        return switch (type) {
            case FAQ -> matched.getReplyTemplate();
            case ORDER_STATUS -> handleOrderStatus(context, matched);
            case HUMAN_HANDOFF -> "已经为您转接人工客服，请稍候。";
        };
    }

    private String handleOrderStatus(ConversationContext context, IntentDefinition matched) {
        String orderId = (String) context.getSlots().getOrDefault("orderId", "A001");
        String fakeStatus = "订单" + orderId + " 已出库，预计 2 天内送达";
        return matched.getReplyTemplate().replace("${orderStatus}", fakeStatus);
    }
}
