package com.assignment.smartloading.decision;

import java.util.List;

public class DecisionOutcome {

    private final DecisionOutcomeType type;
    private final List<String> categories;
    private final String description;

    public DecisionOutcome(DecisionOutcomeType type,
                           List<String> categories,
                           String description) {
        this.type = type;
        this.categories = categories;
        this.description = description;
    }

    public DecisionOutcomeType getType() { return type; }
    public List<String> getCategories() { return categories; }
    public String getDescription() { return description; }
}
