package com.example.dialoguedemo.skill;

import com.example.dialoguedemo.model.ConversationContext;
import com.example.dialoguedemo.model.IntentDefinition;

public interface SkillAdapter {

    boolean supports(String skillName);

    String execute(IntentDefinition intent, ConversationContext context);
}
