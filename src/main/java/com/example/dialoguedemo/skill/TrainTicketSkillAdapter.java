package com.example.dialoguedemo.skill;

import com.example.dialoguedemo.model.ConversationContext;
import com.example.dialoguedemo.model.IntentDefinition;
import org.springframework.stereotype.Component;

@Component
public class TrainTicketSkillAdapter implements SkillAdapter {

    private static final String SKILL_NAME = "train_ticket_12306";

    @Override
    public boolean supports(String skillName) {
        return SKILL_NAME.equalsIgnoreCase(skillName);
    }

    @Override
    public String execute(IntentDefinition intent, ConversationContext context) {
        String destination = String.valueOf(context.getSlots().getOrDefault("destination", "目的地"));
        String departureCity = String.valueOf(context.getSlots().getOrDefault("departureCity", "出发地"));
        String date = String.valueOf(context.getSlots().getOrDefault("departureDate", "最近日期"));
        return "正在为您打开 12306，查询 " + date + " 从 " + departureCity + " 前往 " + destination
                + " 的可选车次：G101 08:00、G135 10:30、D321 12:15。";
    }
}
