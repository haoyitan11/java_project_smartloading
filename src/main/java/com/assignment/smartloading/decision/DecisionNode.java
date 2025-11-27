package com.assignment.smartloading.decision;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class DecisionNode {

    private final String name;
    private final Predicate<RecommendationContext> rule;
    private final Consumer<RecommendationContext> action;

    private DecisionNode nextIfTrue;
    private DecisionNode nextIfFalse;

    public DecisionNode(String name,
                        Predicate<RecommendationContext> rule,
                        Consumer<RecommendationContext> action) {
        this.name = name;
        this.rule = rule;
        this.action = action;
    }

    public DecisionNode nextIfTrue(DecisionNode n) { this.nextIfTrue = n; return this; }
    public DecisionNode nextIfFalse(DecisionNode n) { this.nextIfFalse = n; return this; }

    public void evaluate(RecommendationContext ctx) {
        ctx.steps.add(name);

        if (action != null) action.accept(ctx);

        if (!ctx.needMore()) return;

        boolean result = (rule == null) || rule.test(ctx);

        if (result && nextIfTrue != null) nextIfTrue.evaluate(ctx);
        if (!result && nextIfFalse != null) nextIfFalse.evaluate(ctx);
    }
}
