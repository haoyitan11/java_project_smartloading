package com.assignment.smartloading.decision;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class DecisionNode {

    private final String name;
    private final Predicate<RecommendationContext> rule;
    private final Consumer<RecommendationContext> action;

    private DecisionNode next;

    public DecisionNode(String name,
                        Predicate<RecommendationContext> rule,
                        Consumer<RecommendationContext> action) {
        this.name = name;
        this.rule = rule;
        this.action = action;
    }

    public DecisionNode next(DecisionNode n) {
        this.next = n;
        return n;
    }

    public void evaluate(RecommendationContext ctx) {
        ctx.steps.add(name);

        boolean ok = (rule == null) || rule.test(ctx);
        if (ok && action != null) action.accept(ctx);

        if (ctx.needMore15() && next != null) {
            next.evaluate(ctx);
        }
    }
}
