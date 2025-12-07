package com.example.dialoguedemo.skill;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SkillAdapterRegistry {

    private final List<SkillAdapter> adapters;

    public SkillAdapterRegistry(List<SkillAdapter> adapters) {
        this.adapters = adapters;
    }

    public Optional<SkillAdapter> resolve(String skillName) {
        if (skillName == null || skillName.isBlank()) {
            return Optional.empty();
        }
        return adapters.stream()
                .filter(adapter -> adapter.supports(skillName))
                .findFirst();
    }
}
