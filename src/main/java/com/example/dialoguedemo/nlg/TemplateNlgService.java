package com.example.dialoguedemo.nlg;

import com.example.dialoguedemo.dm.policy.PolicyDecision;
import com.example.dialoguedemo.model.ActionType;
import com.example.dialoguedemo.model.ConversationContext;
import com.example.dialoguedemo.model.IntentDefinition;
import com.example.dialoguedemo.model.SlotDefinition;
import com.example.dialoguedemo.skill.SkillAdapterRegistry;
import org.springframework.stereotype.Component;

/**
 * 负责将策略输出映射为自然语言回复，可替换为更复杂的 NLG 引擎。
 */
@Component
public class TemplateNlgService {

    private final SkillAdapterRegistry skillAdapterRegistry;

    public TemplateNlgService(SkillAdapterRegistry skillAdapterRegistry) {
        this.skillAdapterRegistry = skillAdapterRegistry;
    }

    public String render(PolicyDecision decision, ConversationContext context) {
        if (decision.isFallback()) {
            return "抱歉没有理解您的意思，我们的客服稍后联系您。";
        }
        if (decision.requiresSlotPrompt()) {
            SlotDefinition slot = decision.getPendingSlot();
            if (slot.getPrompt() != null && !slot.getPrompt().isBlank()) {
                return slot.getPrompt();
            }
            return "请提供" + slot.getName();
        }
        IntentDefinition intent = decision.getIntent();
        ActionType type = intent.getActionType();
        return switch (type) {
            case FAQ -> intent.getReplyTemplate();
            case ORDER_STATUS -> handleOrderStatus(context, intent);
            case HUMAN_HANDOFF -> "已经为您转接人工客服，请稍候。";
            case SKILL -> executeSkill(intent, context);
        };
    }

    private String handleOrderStatus(ConversationContext context, IntentDefinition intent) {
        Object orderId = context.getSlots().getOrDefault("orderId", "A001");
        String fakeStatus = "订单" + orderId + " 已出库，预计 2 天内送达";
        return intent.getReplyTemplate().replace("${orderStatus}", fakeStatus);
    }

    private String executeSkill(IntentDefinition intent, ConversationContext context) {
        return skillAdapterRegistry.resolve(intent.getSkillName())
                .map(adapter -> adapter.execute(intent, context))
                .orElseGet(() -> intent.getReplyTemplate() != null
                        ? intent.getReplyTemplate()
                        : "技能暂时不可用，请稍后再试。");
    }
}
