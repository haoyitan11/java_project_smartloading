package com.assignment.smartloading.decision;

import java.util.List;
import java.util.function.Predicate;

public class DecisionTreeNode {

    private final String name;
    private final Predicate<DecisionContext> rule;
    private DecisionTreeNode trueNode;
    private DecisionTreeNode falseNode;
    private DecisionOutcome outcome;

    public DecisionTreeNode(String name, Predicate<DecisionContext> rule) {
        this.name = name;
        this.rule = rule;
    }

    public DecisionTreeNode trueNode(DecisionTreeNode n) { this.trueNode = n; return this; }

    public DecisionTreeNode falseNode(DecisionTreeNode n) { this.falseNode = n; return this; }

    public DecisionTreeNode outcome(DecisionOutcome o) { this.outcome = o; return this; }

    public DecisionOutcome evaluate(DecisionContext ctx, List<String> steps) {
        steps.add(name);

        if (outcome != null) return outcome;

        boolean result = rule.test(ctx);

        if (result) return trueNode.evaluate(ctx, steps);

        return falseNode.evaluate(ctx, steps);
    }
}
